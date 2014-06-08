package cz.cokrtvac.webgephi.api.model.property.basic;

import cz.cokrtvac.webgephi.api.model.property.PropertyValue;
import cz.cokrtvac.webgephi.api.model.property.attribute.AttributePropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlTransient;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 12. 5. 2014
 * Time: 22:36
 */
@XmlTransient
public abstract class BasicPropertyValue extends PropertyValue implements Comparable<BasicPropertyValue> {
    protected Logger log = LoggerFactory.getLogger(getClass());

    public abstract Object getValue();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasicPropertyValue that = (BasicPropertyValue) o;

        if (getValue() != null ? !getValue().equals(that.getValue()) : that.getValue() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getValue() != null ? getValue().hashCode() : 0;
    }

    // Static factory
    // Factory for list creation
    public static Map<String, Factory> factories = initFactories();

    private static Map<String, Factory> initFactories() {
        Map<String, Factory> f = new HashMap<String, Factory>();
        f.put("boolean", new Factory() {

            @Override
            public BasicPropertyValue createBasicPropertyValue(String attributeValue) {
                return new BooleanPropertyValue(Boolean.valueOf(attributeValue));
            }
        });

        f.put("double", new Factory() {

            @Override
            public BasicPropertyValue createBasicPropertyValue(String attributeValue) {
                return new DoublePropertyValue(Double.valueOf(attributeValue));
            }
        });

        f.put("float", new Factory() {

            @Override
            public BasicPropertyValue createBasicPropertyValue(String attributeValue) {
                return new FloatPropertyValue(Float.valueOf(attributeValue));
            }
        });

        f.put("string", new Factory() {

            @Override
            public BasicPropertyValue createBasicPropertyValue(String attributeValue) {
                return new StringPropertyValue(attributeValue);
            }
        });

        Factory intFactory = new Factory() {
            @Override
            public BasicPropertyValue createBasicPropertyValue(String attributeValue) {
                return new IntegerPropertyValue(Integer.valueOf(attributeValue));
            }
        };
        f.put("int", intFactory);
        f.put("integer", intFactory);
        return f;
    }

    public static BasicPropertyValue createBasicPropertyValue(AttributePropertyValue.Attribute attribute, String attributeValue) {
        if (!factories.containsKey(attribute.getValueType())) {
            throw new IllegalArgumentException("No Basic property value implementation for " + attribute.getValueType() + " type");
        }
        return factories.get(attribute.getValueType()).createBasicPropertyValue(attributeValue);
    }

    private interface Factory {
        public BasicPropertyValue createBasicPropertyValue(String attributeValue);
    }
}
