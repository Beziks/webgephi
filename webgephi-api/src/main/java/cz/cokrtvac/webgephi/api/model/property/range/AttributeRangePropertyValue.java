package cz.cokrtvac.webgephi.api.model.property.range;

import cz.cokrtvac.webgephi.api.model.property.ListPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.attribute.AttributePropertyValue;
import cz.cokrtvac.webgephi.api.model.property.basic.IntegerPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.basic.NumberPropertyValue;
import org.w3c.dom.Document;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.transform.TransformerException;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 2. 6. 2014
 * Time: 17:25
 * <p/>
 * Range of values in attribute
 */
public class AttributeRangePropertyValue extends AttributePropertyValue {
    public static final String TYPE = "attributeRange";

    private RangePropertyValue range = new RangePropertyValue(new IntegerPropertyValue(0), new IntegerPropertyValue(1));

    public AttributeRangePropertyValue() {
    }

    public AttributeRangePropertyValue(String attributeId) {
        super(attributeId);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @XmlElement(required = true)
    public RangePropertyValue getRange() {
        return range;
    }

    public void setRange(RangePropertyValue range) {
        this.range = range;
    }

    public void initRange(Document gexfGraph) throws TransformerException {
        NumberPropertyValue min = getMin(gexfGraph);
        if (min != null) {
            range.setFrom(min);
            range.setTo(getMax(gexfGraph));
        }
    }

    public NumberPropertyValue getMin(Document gexfGraph) throws TransformerException {
        ListPropertyValue all = getAllAttributeValues(gexfGraph);
        if (all == null || all.getValues().isEmpty()) {
            return null;
        }
        return (NumberPropertyValue) all.getValues().get(0);
    }

    public NumberPropertyValue getMax(Document gexfGraph) throws TransformerException {
        ListPropertyValue all = getAllAttributeValues(gexfGraph);
        if (all == null || all.getValues().isEmpty()) {
            return null;
        }
        return (NumberPropertyValue) all.getValues().get(all.getValues().size() - 1);
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
