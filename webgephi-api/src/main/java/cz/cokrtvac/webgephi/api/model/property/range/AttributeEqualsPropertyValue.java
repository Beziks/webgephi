package cz.cokrtvac.webgephi.api.model.property.range;

import cz.cokrtvac.webgephi.api.model.property.ListPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.attribute.AttributePropertyValue;
import cz.cokrtvac.webgephi.api.model.property.basic.*;
import org.w3c.dom.Document;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.transform.TransformerException;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 2. 6. 2014
 * Time: 17:25
 */
public class AttributeEqualsPropertyValue extends AttributePropertyValue {
    public static final String TYPE = "attributeValue";

    private BasicPropertyValue attributeValue;

    public AttributeEqualsPropertyValue() {
    }

    public AttributeEqualsPropertyValue(String attributeId) {
        super(attributeId);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @XmlElements({
            @XmlElement(name = DoublePropertyValue.TYPE, type = DoublePropertyValue.class, required = true),
            @XmlElement(name = FloatPropertyValue.TYPE, type = FloatPropertyValue.class, required = true),
            @XmlElement(name = IntegerPropertyValue.TYPE, type = IntegerPropertyValue.class, required = true),
            @XmlElement(name = BooleanPropertyValue.TYPE, type = BooleanPropertyValue.class, required = true),
            @XmlElement(name = StringPropertyValue.TYPE, type = StringPropertyValue.class, required = true),
    })
    public BasicPropertyValue getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(BasicPropertyValue value) {
        this.attributeValue = value;
    }

    public void initValue(Document gexfGraph) throws TransformerException {
        ListPropertyValue all = getAllAttributeValues(gexfGraph);
        if (all != null && !all.getValues().isEmpty()) {
            attributeValue = all.getValues().get(0);
        }
    }
}
