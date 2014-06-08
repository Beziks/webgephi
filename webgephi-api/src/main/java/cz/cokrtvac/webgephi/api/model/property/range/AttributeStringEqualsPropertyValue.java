package cz.cokrtvac.webgephi.api.model.property.range;

import cz.cokrtvac.webgephi.api.model.property.basic.StringPropertyValue;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 2. 6. 2014
 * Time: 17:25
 */
public class AttributeStringEqualsPropertyValue extends AttributeEqualsPropertyValue {
    public static final String TYPE = "attributeStringValue";

    public AttributeStringEqualsPropertyValue() {
    }

    public AttributeStringEqualsPropertyValue(String attributeId) {
        super(attributeId);
        setAttributeValue(new StringPropertyValue(""));
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
                return attribute.getValueType().equalsIgnoreCase("string");
            }
        });
    }
}
