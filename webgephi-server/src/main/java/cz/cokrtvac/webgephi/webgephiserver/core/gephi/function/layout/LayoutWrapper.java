package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.layout;

import cz.cokrtvac.webgephi.api.util.StringUtil;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.GraphFunction;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.GraphFunctionProperty;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutProperty;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 17. 5. 2014
 * Time: 17:08
 */
public class LayoutWrapper implements GraphFunction {
    private Layout layout;
    List<GraphFunctionProperty> properties = new ArrayList<GraphFunctionProperty>();

    public LayoutWrapper(Layout layout) {
        this.layout = layout;

        for (LayoutProperty p : layout.getProperties()) {
            properties.add(new LayoutPropertyAdapter(p));
        }
    }

    @Override
    public String getId() {
        return StringUtil.uriSafe(getName());
    }

    @Override
    public String getName() {
        return layout.getBuilder().getName();
    }

    @Override
    public String getDescription() {
        return null;
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
    public LayoutWrapper copy() {
        Layout l = layout.getBuilder().buildLayout();
        l.resetPropertiesValues();
        return new LayoutWrapper(l);
    }

    public Layout getLayout() {
        return layout;
    }

    public static class LayoutPropertyAdapter<T> implements GraphFunctionProperty<T> {
        private LayoutProperty property;

        public LayoutPropertyAdapter(LayoutProperty prop) {
            this.property = prop;
        }

        @Override
        public String getId() {
            return property.getCanonicalName();
        }

        @Override
        public String getName() {
            return property.getProperty().getName();
        }

        @Override
        public String getDescription() {
            return property.getProperty().getShortDescription();
        }

        @Override
        public Class<T> getValueType() {
            return property.getProperty().getValueType();
        }

        @Override
        public T getValue() {
            try {
                return (T) property.getProperty().getValue();
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }

        @Override
        public void setValue(T value) {
            try {
                property.getProperty().setValue(value);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
    }
}
