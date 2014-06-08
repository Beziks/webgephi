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
public class StringPropertyValue extends BasicPropertyValue {
    public static final String TYPE = "string";

    private String value;

    public StringPropertyValue() {
    }

    public StringPropertyValue(String value) {
        this.setValue(value);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @XmlAttribute(required = true)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int compareTo(BasicPropertyValue o) {
        try {
            return getValue().compareTo((String) o.getValue());
        } catch (Exception e) {
            return 0;
        }
    }
}
