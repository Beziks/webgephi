package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.converter;

import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.partition.PartitionSelectPropertyValue;
import cz.cokrtvac.webgephi.webgephiserver.core.WebgephiXmlFactory;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.FunctionProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.GraphFunctionProperty;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.FilterWrapper;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.FiltersPool;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import org.gephi.partition.api.NodePartition;
import org.gephi.partition.api.Part;
import org.gephi.partition.api.Partition;

import java.util.List;

/**
 * partition-filter =============================================================
 * <property id="constructor-param-0">
 * <attribute attributeId="modularity_class"/>
 * <p/>
 * <property id="constructor-param-1">
 * <nodePartition attributeId="modularity_class"/>
 * <p/>
 * <property id="column">
 * <attribute attributeId="modularity_class"/>
 * <p/>
 * <property id="parts">
 * <list />
 */
class PartitionFilterCustomConverter extends FilterCustomConverter.FilterBaseCustomConverter {

    @Override
    public FilterXml convert(FilterWrapper filterWrapper) {
        FilterXml xml = createBase(filterWrapper);

        // constructor-param-1, <nodePartition attributeId="id-of-attribute-column"/>
        GraphFunctionProperty<NodePartition> fp = filterWrapper.getProperty("constructor-param-1");
        PropertyXml<PartitionSelectPropertyValue> p = WebgephiXmlFactory.createXml(fp, new PartitionSelectPropertyValue(fp.getValue().getColumn().getId()));
        p.setId("selected-partitions");
        p.setName("Selected partitions");
        p.setDescription("Select partitions which will be used in filter");

        xml.addProperty(p);
        return xml;
    }

    @Override
    public FilterWrapper convert(FilterXml filterXml, FiltersPool pool, WorkspaceWrapper ww) throws ValidationException {
        FilterWrapper function = pool.createNew(filterXml);
        PartitionSelectPropertyValue xmlValue = (PartitionSelectPropertyValue) filterXml.getProperties().get(0).getValue();

        Partition partition = FunctionProcessor.toPartition(xmlValue, ww);
        List<Part> parts = FunctionProcessor.toParts(xmlValue, ww);

        function.getProperty("constructor-param-0").setValue(partition.getColumn());
        function.getProperty("constructor-param-1").setValue(partition);
        function.getProperty("column").setValue(partition.getColumn());
        function.getProperty("parts").setValue(parts);
        return function;
    }
}
