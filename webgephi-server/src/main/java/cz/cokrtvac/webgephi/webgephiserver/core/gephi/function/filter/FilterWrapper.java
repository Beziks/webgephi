package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter;

import cz.cokrtvac.webgephi.api.util.IOUtil;
import cz.cokrtvac.webgephi.api.util.Log;
import cz.cokrtvac.webgephi.api.util.StringUtil;
import cz.cokrtvac.webgephi.webgephiserver.core.InitializationException;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.GephiImporter;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.GraphFunction;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.GraphFunctionProperty;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.GephiWorkspaceProvider;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.filters.spi.Category;
import org.gephi.filters.spi.Filter;
import org.gephi.filters.spi.FilterBuilder;
import org.gephi.filters.spi.FilterProperty;
import org.gephi.io.importer.impl.ImportControllerImpl;
import org.gephi.partition.api.EdgePartition;
import org.gephi.partition.api.NodePartition;
import org.gephi.partition.impl.PartitionFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 14. 5. 2014
 * Time: 22:37
 */
public class FilterWrapper implements GraphFunction {
    private static Logger log = Log.get(FilterWrapper.class);

    private Class<? extends FilterBuilder> filterBuilderClass;
    private FilterBuilder filterBuilder;
    private Object[] filterBuilderConstructorParams = null;
    private Filter filter;

    private List<GraphFunctionProperty> properties;
    private WorkspaceWrapper sampleWorkspace;

    @Override
    public String getId() {
        return StringUtil.uriSafe(getBaseName());
    }

    @Override
    public String getName() {
        return getBaseName();
    }

    private String getBaseName() {
        if (filterBuilder.getName().contains("<") || filterBuilder.getName().toLowerCase().contains("modularity class")) {
            String s = StringUtil.splitCamelCase(filterBuilder.getClass().getSimpleName());
            s = s.replaceAll("Builder", "");
            return s.trim();
        }
        return filterBuilder.getName();
    }

    private String getCategory() {
        Category cat = filterBuilder.getCategory();
        String c = "";
        while (cat != null) {
            c += cat.getName();
            cat = cat.getParent();
            if (cat != null) {
                c += "-";
            }
        }
        return c;
    }

    public Filter getFilter() {
        return filter;
    }

    public Filter createFilter() throws Exception {
        Constructor<?> fbConstr = filterBuilderClass.getConstructors()[0];
        fbConstr.setAccessible(true);
        FilterBuilder fb = (FilterBuilder) fbConstr.newInstance(filterBuilderConstructorParams);
        Filter f = fb.getFilter();
        if (f.getProperties() != null) {
            for (FilterProperty p : f.getProperties()) {
                p.setValue(getProperty(StringUtil.uriSafe(p.getName())).getValue());
            }
        }
        return f;
    }

    @Override
    public String getDescription() {
        String desc = "Category: " + getCategory();
        if (filterBuilder.getDescription() != null) {
            desc += "; " + filterBuilder.getDescription();
        }
        return desc;
    }

    @Override
    public List<GraphFunctionProperty> getProperties() {
        return properties;
    }

    @Override
    public GraphFunctionProperty getProperty(String id) {
        for (GraphFunctionProperty p : getProperties()) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public GraphFunction copy() {
        return new FilterWrapper(filterBuilderClass);
    }

    private FilterWrapper() {
        try {
            GephiImporter gephiImporter = new GephiImporter(log, new ImportControllerImpl());
            sampleWorkspace = new GephiWorkspaceProvider().getWorkspace();
            gephiImporter.importGexf(IOUtil.readAsString(getClass().getClassLoader().getResourceAsStream("gexf/misserables_full.gexf")), sampleWorkspace);
        } catch (IOException e) {
            throw new InitializationException(e);
        }
    }

    /**
     * Creates filter of defined class with all necessary parameters.
     * Use only to show filter required parameters.
     * To createXml working wrapper, use the other constructor.
     *
     * @param filterBuilderClass
     */
    public FilterWrapper(Class<? extends FilterBuilder> filterBuilderClass) {
        this();
        this.filterBuilderClass = filterBuilderClass;

        try {
            this.properties = loadSampleProperties();
        } catch (Exception e) {
            String msg = "Filter properties cannot be loaded: " + e.getMessage();
            log.error("Filter properties cannot be loaded: " + e.getMessage(), e);
            throw new InitializationException(msg, e);
        }
    }

    @Override
    public String toString() {
        String s = "FilterWrapper " + filterBuilder.getName() + "/" + filter.getName() + "\n";
        for (GraphFunctionProperty p : properties) {
            s += p.getName() + " : " + p.getValueType().getSimpleName() + "\n";
        }
        return s;
    }

    private List<GraphFunctionProperty> loadSampleProperties() throws Exception {
        List<GraphFunctionProperty> out = new ArrayList<GraphFunctionProperty>();

        filterBuilder = null;

        for (Constructor<?> c : filterBuilderClass.getConstructors()) {
            if (c.getParameterTypes().length == 0) {
                c.setAccessible(true);
                filterBuilder = (FilterBuilder) c.newInstance();
            }
        }

        if (filterBuilder == null) {
            Constructor<?> c = filterBuilderClass.getConstructors()[0];
            c.setAccessible(true);

            filterBuilderConstructorParams = new Object[c.getParameterTypes().length];
            int index = 0;
            for (Class<?> p : c.getParameterTypes()) {
                FilterBuilderConstructorProperty prop = new FilterBuilderConstructorProperty(p, index++);
                out.add(prop);

                if (p.isAssignableFrom(AttributeColumn.class)) {
                    prop.setValue(getNodeAttribute());
                } else if (p.isAssignableFrom(NodePartition.class)) {
                    prop.setValue(getNodePartition());
                } else if (p.isAssignableFrom(EdgePartition.class)) {
                    prop.setValue(getEdgePartition());
                } else {
                    throw new IllegalArgumentException("Unexpected constructor attribute: " + filterBuilderClass + ":" + (index - 1) + ":" + p.getName());
                }
            }

            filterBuilder = (FilterBuilder) c.newInstance(filterBuilderConstructorParams);
        }

        filter = filterBuilder.getFilter();
        if (filter.getProperties() != null) {
            for (FilterProperty p : filter.getProperties()) {
                out.add(new RegularFilterWrapperProperty(p));
            }
        }

        return out;
    }

    public static interface FilterWrapperProperty<T> extends GraphFunctionProperty<T> {
        public String getId();

        public String getName();

        public String getDescription();

        public Class<T> getValueType();

        public T getValue();

        public void setValue(T value);
    }

    public class RegularFilterWrapperProperty<T> implements FilterWrapperProperty<T> {
        private FilterProperty property;

        public RegularFilterWrapperProperty(FilterProperty property) {
            this.property = property;
        }

        @Override
        public String getId() {
            return StringUtil.uriSafe(getName());
        }

        @Override
        public String getName() {
            return property.getName();
        }

        @Override
        public String getDescription() {
            return "Regular filter property";
        }

        @Override
        public Class<T> getValueType() {
            return property.getValueType();
        }

        @Override
        public T getValue() {
            return (T) property.getValue();
        }

        @Override
        public void setValue(T value) {
            if (!property.getValueType().isAssignableFrom(value.getClass()) && property.getValueType().getSimpleName().equalsIgnoreCase(value.getClass().getSimpleName())) {
                log.warn("Value class mismatch: " + value.getClass().getSimpleName() + " != " + property.getValueType());
            }
            property.setValue(value);
        }
    }

    public class FilterBuilderConstructorProperty<T> implements FilterWrapperProperty<T> {
        private Class<T> clazz;
        private int index;

        public FilterBuilderConstructorProperty(Class<T> clazz, int index) {
            this.clazz = clazz;
            this.index = index;
        }

        @Override
        public String getId() {
            return StringUtil.uriSafe(getName());
        }

        @Override
        public String getName() {
            return "constructor param " + index;
        }

        @Override
        public String getDescription() {
            return index + ". parameter of constructor";
        }

        @Override
        public Class<T> getValueType() {
            return clazz;
        }

        @Override
        public T getValue() {
            return (T) filterBuilderConstructorParams[index];
        }

        @Override
        public void setValue(T value) {
            filterBuilderConstructorParams[index] = value;
        }
    }

    // SAMPLE DATA TO INIT BUILDERS
    public AttributeColumn getNodeAttribute() {
        AttributeColumn attr = sampleWorkspace.getAttributeModel().getNodeTable().getColumn(2);
        log.info("Sample node attribute: " + attr.getId());
        return attr;
    }

    public AttributeColumn getEdgeAttribute() {
        AttributeColumn attr = sampleWorkspace.getAttributeModel().getEdgeTable().getColumn(0);
        log.info("Sample edge attribute: " + attr.getId());
        return attr;
    }

    public NodePartition getNodePartition() {
        AttributeColumn coll = getNodeAttribute();
        if (PartitionFactory.isPartitionColumn(coll)) {
            NodePartition p = PartitionFactory.createNodePartition(coll);
            log.info("Sample node partition: " + p);
            return p;
        } else {
            throw new InitializationException("Attribute " + coll + " should be a partition");
        }
    }

    public EdgePartition getEdgePartition() {
        AttributeColumn coll = getEdgeAttribute();
        if (PartitionFactory.isPartitionColumn(coll)) {
            EdgePartition p = PartitionFactory.createEdgePartition(coll);
            log.info("Sample edge partition: " + p);
            return p;
        } else {
            throw new InitializationException("Attribute " + coll + " should be a partition");
        }
    }

}
