package cz.cokrtvac.webgephi.api.model.property.basic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 12. 5. 2014
 * Time: 22:38
 */
@XmlAccessorType(XmlAccessType.NONE)
public class BooleanPropertyValue extends BasicPropertyValue {
    public static final String TYPE = "boolean";

    private Boolean value;

    public BooleanPropertyValue() {
    }

    public BooleanPropertyValue(Boolean value) {
        this.setValue(value);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @XmlAttribute(required = true)
    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public int compareTo(BasicPropertyValue o) {
        try {
            return getValue().compareTo((Boolean) o.getValue());
        } catch (Exception e) {
            return 0;
        }
    }
}
