package cz.cokrtvac.webgephi.api.model.property.attribute;

import org.w3c.dom.Document;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.transform.TransformerException;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 12. 5. 2014
 * Time: 22:38
 */
@XmlAccessorType(XmlAccessType.NONE)
public class EdgeAttributePropertyValue extends AttributePropertyValue {
    public static final String TYPE = "edgeAttribute";

    public EdgeAttributePropertyValue() {
    }

    public EdgeAttributePropertyValue(String attributeId) {
        this.setAttributeId(attributeId);
    }

    @Override
    public List<Attribute> getPossibleAttributes(Document gexfGraph) throws TransformerException {
        return filter(super.getPossibleAttributes(gexfGraph), new Filter() {
            @Override
            public boolean filter(Attribute attribute) {
                return attribute.getAttributeType() == AttributeType.EDGE;
            }
        });
    }
}
