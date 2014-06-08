package cz.cokrtvac.webgephi.api.model.property.range;

import cz.cokrtvac.webgephi.api.model.property.ComplexPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.basic.NumberPropertyValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 12. 5. 2014
 * Time: 22:38
 */
@XmlAccessorType(XmlAccessType.NONE)
public class RangePropertyValue extends ComplexPropertyValue {
    public static final String TYPE = "range";

    private NumberPropertyValue from;
    private NumberPropertyValue to;

    public RangePropertyValue() {
    }

    public RangePropertyValue(NumberPropertyValue from, NumberPropertyValue to) {
        this.from = from;
        this.to = to;
    }

    public String getType() {
        return TYPE;
    }

    @XmlElement
    public NumberPropertyValue getFrom() {
        return from;
    }

    public void setFrom(NumberPropertyValue from) {
        this.from = from;
    }

    @XmlElement(required = true)
    public NumberPropertyValue getTo() {
        return to;
    }

    public void setTo(NumberPropertyValue to) {
        this.to = to;
    }
}
