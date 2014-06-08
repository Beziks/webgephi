package cz.cokrtvac.webgephi.api.model.property;

import cz.cokrtvac.webgephi.api.model.property.attribute.AttributePropertyValue;
import cz.cokrtvac.webgephi.api.model.property.attribute.EdgeAttributePropertyValue;
import cz.cokrtvac.webgephi.api.model.property.attribute.NodeAttributePropertyValue;
import cz.cokrtvac.webgephi.api.model.property.basic.*;
import cz.cokrtvac.webgephi.api.model.property.partition.*;
import cz.cokrtvac.webgephi.api.model.property.range.*;
import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.*;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 3.6.13
 * Time: 18:23
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"restServiceDiscovery", "id", "name", "description", "valueWrapper"})
public class PropertyXml<T extends PropertyValue> {
    private String id;
    private String name;
    private String description;
    private ValueWrapper<T> value;

    private RESTServiceDiscovery restServiceDiscovery;

    public PropertyXml() {
    }

    public PropertyXml(String id, String name, String description, T value) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.value = new ValueWrapper<T>();
        this.value.setValue(value);
    }

    @XmlID
    @XmlAttribute(required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(required = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(required = true, name = "value")
    public ValueWrapper<T> getValueWrapper() {
        return value;
    }

    public void setValueWrapper(ValueWrapper<T> value) {
        this.value = value;
    }

    public void setValue(T value) {
        if (this.value == null) {
            this.value = new ValueWrapper<T>();
        }
        this.value.setValue(value);
    }

    public T getValue() {
        return value.getValue();
    }

    @Override
    public String toString() {
        return "PropertyXml{name='" + name + '\'' + ", description='" + description + '\'' + ", value=" + value + '}';
    }

    @XmlElementRef
    public RESTServiceDiscovery getRestServiceDiscovery() {
        return restServiceDiscovery;
    }

    public void setRestServiceDiscovery(RESTServiceDiscovery restServiceDiscovery) {
        this.restServiceDiscovery = restServiceDiscovery;
    }

    // Value wrapper ========================================================================================
    public static class ValueWrapper<T extends PropertyValue> {
        private T value;

        @XmlElements({
                @XmlElement(name = AttributePropertyValue.TYPE, type = AttributePropertyValue.class, required = true),
                @XmlElement(name = NodeAttributePropertyValue.TYPE, type = NodeAttributePropertyValue.class, required = true),
                @XmlElement(name = EdgeAttributePropertyValue.TYPE, type = EdgeAttributePropertyValue.class, required = true),
                @XmlElement(name = BooleanPropertyValue.TYPE, type = BooleanPropertyValue.class, required = true),
                @XmlElement(name = ColorPropertyValue.TYPE, type = ColorPropertyValue.class, required = true),
                @XmlElement(name = DoublePropertyValue.TYPE, type = DoublePropertyValue.class, required = true),
                @XmlElement(name = FloatPropertyValue.TYPE, type = FloatPropertyValue.class, required = true),
                @XmlElement(name = IntegerPropertyValue.TYPE, type = IntegerPropertyValue.class, required = true),
                @XmlElement(name = StringPropertyValue.TYPE, type = StringPropertyValue.class, required = true),
                @XmlElement(name = RangePropertyValue.TYPE, type = RangePropertyValue.class, required = true),
                @XmlElement(name = NodePartitionPropertyValue.TYPE, type = NodePartitionPropertyValue.class, required = true),
                @XmlElement(name = EdgePartitionPropertyValue.TYPE, type = EdgePartitionPropertyValue.class, required = true),
                @XmlElement(name = ListPropertyValue.TYPE, type = ListPropertyValue.class, required = true),
                @XmlElement(name = PartitionSelectPropertyValue.TYPE, type = PartitionSelectPropertyValue.class, required = true),
                @XmlElement(name = NodePartitionSelectPropertyValue.TYPE, type = NodePartitionSelectPropertyValue.class, required = true),
                @XmlElement(name = EdgePartitionSelectPropertyValue.TYPE, type = EdgePartitionSelectPropertyValue.class, required = true),
                @XmlElement(name = AttributeRangePropertyValue.TYPE, type = AttributeRangePropertyValue.class, required = true),
                @XmlElement(name = AttributeEqualsPropertyValue.TYPE, type = AttributeEqualsPropertyValue.class, required = true),
                @XmlElement(name = AttributeBooleanEqualsPropertyValue.TYPE, type = AttributeBooleanEqualsPropertyValue.class, required = true),
                @XmlElement(name = AttributeStringEqualsPropertyValue.TYPE, type = AttributeStringEqualsPropertyValue.class, required = true),
                @XmlElement(name = AttributeNumberEqualsPropertyValue.TYPE, type = AttributeNumberEqualsPropertyValue.class, required = true),
        })
        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }
}


