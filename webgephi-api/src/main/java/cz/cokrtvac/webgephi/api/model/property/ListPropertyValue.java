package cz.cokrtvac.webgephi.api.model.property;

import cz.cokrtvac.webgephi.api.model.property.basic.BasicPropertyValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 12. 5. 2014
 * Time: 22:38
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ListPropertyValue extends ComplexPropertyValue {
    public static final String TYPE = "list";

    private List<BasicPropertyValue> values;

    public ListPropertyValue() {
        setValues(values);
    }

    public String getType() {
        return TYPE;
    }

    @XmlElement(name = "partition")
    public List<BasicPropertyValue> getValues() {
        if (values == null) {
            values = new ArrayList<BasicPropertyValue>();
        }
        return values;
    }

    public void setValues(List<? extends BasicPropertyValue> values) {
        this.values = (List<BasicPropertyValue>) values;
    }


}
