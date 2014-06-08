package cz.cokrtvac.webgephi.api.model.property.partition;

import cz.cokrtvac.webgephi.api.model.property.attribute.AttributePropertyValue;
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
public class NodePartitionPropertyValue extends PartitionPropertyValue {
    public static final String TYPE = "nodePartition";

    public NodePartitionPropertyValue() {
    }

    public NodePartitionPropertyValue(String attributeId) {
        super(attributeId);
    }

    public String getType() {
        return TYPE;
    }

    @Override
    public List<AttributePropertyValue.Attribute> getPossibleAttributes(Document gexfGraph) throws TransformerException {
        return filter(super.getPossibleAttributes(gexfGraph), new AttributePropertyValue.Filter() {
            @Override
            public boolean filter(AttributePropertyValue.Attribute attribute) {
                return attribute.getAttributeType() == AttributePropertyValue.AttributeType.NODE;
            }
        });
    }
}
