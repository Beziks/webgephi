package cz.cokrtvac.webgephi.api.model.property.range;

import cz.cokrtvac.webgephi.api.model.property.basic.BooleanPropertyValue;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 2. 6. 2014
 * Time: 17:25
 */
public class AttributeBooleanEqualsPropertyValue extends AttributeEqualsPropertyValue {
    public static final String TYPE = "attributeBooleanValue";

    public AttributeBooleanEqualsPropertyValue() {
    }

    public AttributeBooleanEqualsPropertyValue(String attributeId) {
        super(attributeId);
        setAttributeValue(new BooleanPropertyValue(Boolean.TRUE));
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<Attribute> getPossibleAttributes(Document gexfGraph) throws TransformerException {
        return filter(super.getPossibleAttributes(gexfGraph), new Filter() {
            @Override
            public boolean filter(Attribute attribute) {
                return attribute.getValueType().equalsIgnoreCase("boolean");
            }
        });
    }
}
