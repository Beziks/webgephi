package cz.cokrtvac.webgephi.api.model.property;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 12. 5. 2014
 * Time: 22:38
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ColorPropertyValue extends ComplexPropertyValue {
    public static final String TYPE = "color";

    private String hexColor;

    public ColorPropertyValue() {
    }

    public ColorPropertyValue(String value) {
        this.setValue(value);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @XmlValue
    public String getValue() {
        return hexColor;
    }

    public void setValue(String value) {
        this.hexColor = value;
    }
}
