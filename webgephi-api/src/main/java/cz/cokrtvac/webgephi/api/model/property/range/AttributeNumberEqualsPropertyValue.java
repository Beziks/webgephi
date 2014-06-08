package cz.cokrtvac.webgephi.api.model.property.range;

import cz.cokrtvac.webgephi.api.model.property.basic.IntegerPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.basic.NumberPropertyValue;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 2. 6. 2014
 * Time: 17:25
 */
public class AttributeNumberEqualsPropertyValue extends AttributeEqualsPropertyValue {
    public static final String TYPE = "attributeNumberValue";

    public AttributeNumberEqualsPropertyValue() {
    }

    public AttributeNumberEqualsPropertyValue(String attributeId) {
        super(attributeId);
        setAttributeValue(new IntegerPropertyValue(0));
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
                return NumberPropertyValue.NUMBER_TYPES.contains(attribute.getValueType().toLowerCase());
            }
        });
    }
}
