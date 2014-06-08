package cz.cokrtvac.webgephi.api.model.property.partition;

import cz.cokrtvac.webgephi.api.model.property.attribute.AttributePropertyValue;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 31. 5. 2014
 * Time: 23:57
 * <p/>
 * Combines Partition and list.
 * Enumeration of selected partitions of defined attribute
 */
public class NodePartitionSelectPropertyValue extends PartitionSelectPropertyValue {
    public static final String TYPE = "nodePartitionSelect";

    public NodePartitionSelectPropertyValue() {
    }

    public NodePartitionSelectPropertyValue(String attributeId) {
        super(attributeId);
    }

    @Override
    public List<AttributePropertyValue.Attribute> getPossibleAttributes(Document gexfGraph) throws TransformerException {
        return filter(super.getPossibleAttributes(gexfGraph), new AttributePropertyValue.Filter() {
            @Override
            public boolean filter(AttributePropertyValue.Attribute attribute) {
                return attribute.getAttributeType() == AttributeType.NODE;
            }
        });
    }
}
