package cz.cokrtvac.webgephi.webgephiserver.core;

import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.property.ColorPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.PropertyValue;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.attribute.AttributePropertyValue;
import cz.cokrtvac.webgephi.api.model.property.attribute.EdgeAttributePropertyValue;
import cz.cokrtvac.webgephi.api.model.property.attribute.NodeAttributePropertyValue;
import cz.cokrtvac.webgephi.api.model.property.basic.*;
import cz.cokrtvac.webgephi.api.model.property.partition.EdgePartitionPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.partition.NodePartitionPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.partition.PartitionPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.partition.PartitionSelectPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.range.RangePropertyValue;
import cz.cokrtvac.webgephi.api.model.user.UserXml;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.GraphFunctionProperty;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.GraphEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.User;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.filters.api.Range;
import org.gephi.partition.api.EdgePartition;
import org.gephi.partition.api.NodePartition;
import org.gephi.partition.api.Partition;

import java.awt.*;
import java.net.URI;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 30.1.14
 * Time: 22:33
 */
public class WebgephiXmlFactory {
    public static GraphDetailXml createXml(GraphEntity entity) {
        GraphDetailXml xml = new GraphDetailXml();
        xml.setId(entity.getId());
        xml.setName(entity.getName());
        xml.setCreated(entity.getCreated());
        xml.setOwner(entity.getOwner().getUsername());
        if (entity.getStatisticsReport() != null) {
            xml.restServiceDiscovery.addLink(URI.create(""), GraphDetailXml.STATISTICS_REPORT);
        }

        if (entity.getParent() != null) {
            GraphDetailXml xmlParent = new GraphDetailXml();
            xmlParent.setName(entity.getParent().getName());
            xmlParent.setCreated(entity.getParent().getCreated());
            xmlParent.setId(entity.getParent().getId());
            if (entity.getParent().getStatisticsReport() != null) {
                xmlParent.restServiceDiscovery.addLink(URI.create(""), GraphDetailXml.STATISTICS_REPORT);
            }
            xmlParent.setOwner(entity.getParent().getOwner().getUsername());
            xml.setParent(xmlParent);
        }
        return xml;
    }

    public static UserXml createXml(User user) {
        UserXml x = new UserXml();
        x.setEmail(user.getEmail());
        x.setFirstName(user.getFirstName());
        x.setLastName(user.getLastName());
        x.setPassword(user.getPassword());
        x.setUsername(user.getUsername());
        return x;
    }

    public static <O, T extends PropertyValue> PropertyXml<T> createXml(GraphFunctionProperty<O> property) {
        return createXml(property, (T) createPropertyValue(property.getValueType(), property.getValue(), property.getName(), property.getId()));
    }

    public static <O, T extends PropertyValue> PropertyXml<T> createXml(GraphFunctionProperty<O> property, T propertyValueXml) {
        PropertyXml<T> xml = new PropertyXml<T>();
        xml.setName(property.getName());
        xml.setId(property.getId());
        xml.setDescription(property.getDescription());
        xml.setValue(propertyValueXml);
        return xml;
    }

    private static <T, V extends PropertyValue> V createPropertyValue(Class<T> clazz, T value, String name, String id) {
        if (java.util.List.class.isAssignableFrom(clazz)) {
            return (V) new PartitionSelectPropertyValue();
        }

        if (Partition.class.isAssignableFrom(clazz)) {
            Partition p = (Partition) value;
            if (clazz.isAssignableFrom(NodePartition.class)) {
                return (V) new NodePartitionPropertyValue(p.getColumn().getId());
            }
            if (clazz.isAssignableFrom(EdgePartition.class)) {
                return (V) new EdgePartitionPropertyValue(p.getColumn().getId());
            }
            return (V) new PartitionPropertyValue(p.getColumn().getId());
        }

        if (clazz.isAssignableFrom(Range.class)) {
            Range r = (Range) value;
            if (r == null) {
                r = new Range(0, 1);
            }
            NumberPropertyValue from = (NumberPropertyValue) createPropertyValue(r.getRangeType(), r.getLowerBound(), "from", "from");
            NumberPropertyValue to = (NumberPropertyValue) createPropertyValue(r.getRangeType(), r.getUpperBound(), "to", "to");
            return (V) new RangePropertyValue(from, to);
        }

        if (clazz.isAssignableFrom(AttributeColumn.class)) {
            AttributePropertyValue prop = new AttributePropertyValue();
            if (name.toLowerCase().contains("node") || id.toLowerCase().contains("node")) {
                prop = new NodeAttributePropertyValue();
            } else if (name.toLowerCase().contains("edge") || id.toLowerCase().contains("edge")) {
                prop = new EdgeAttributePropertyValue();
            }

            if (value != null) {
                prop.setAttributeId(((AttributeColumn) value).getId());
            }
            return (V) prop;
        }

        if (clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(boolean.class)) {
            return (V) new BooleanPropertyValue((Boolean) value);
        }

        if (clazz.isAssignableFrom(Color.class)) {
            ColorPropertyValue colorPropertyValue = new ColorPropertyValue();
            if (value != null) {
                Color c = (Color) value;
                String cString = Integer.toHexString(c.getRGB()).toUpperCase();
                if (cString.length() > 6) {
                    cString = cString.substring(cString.length() - 6);
                }
                colorPropertyValue.setValue(cString);
            }
            return (V) colorPropertyValue;
        }

        if (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class)) {
            return (V) new DoublePropertyValue((Double) value);
        }

        if (clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(float.class)) {
            return (V) new FloatPropertyValue((Float) value);
        }

        if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
            return (V) new IntegerPropertyValue((Integer) value);
        }

        if (clazz.isAssignableFrom(String.class)) {
            return (V) new StringPropertyValue((String) value);
        }

        throw new IllegalArgumentException("Unknown property type " + clazz + " | " + value);
    }

    public static User fromXml(UserXml userXml) {
        User u = new User(
                userXml.getUsername(),
                userXml.getPassword(),
                userXml.getEmail(),
                userXml.getFirstName(),
                userXml.getLastName());
        return u;
    }
}
