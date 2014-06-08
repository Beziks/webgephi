package cz.cokrtvac.webgephi.api.model.property.partition;

import cz.cokrtvac.webgephi.api.model.property.attribute.AttributePropertyValue;
import org.w3c.dom.Document;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.transform.TransformerException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 12. 5. 2014
 * Time: 22:38
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PartitionPropertyValue extends AttributePropertyValue {
    public static final String TYPE = "partition";
    protected static final Set<String> PARTITION_TYPES = new HashSet<String>(Arrays.asList(new String[]{"string", "boolean", "int", "integer", "short"}));

    public PartitionPropertyValue() {
    }

    public PartitionPropertyValue(String attributeId) {
        super(attributeId);
    }

    public String getType() {
        return TYPE;
    }

    @Override
    public List<Attribute> getPossibleAttributes(Document gexfGraph) throws TransformerException {
        return filter(super.getPossibleAttributes(gexfGraph), new Filter() {
            @Override
            public boolean filter(Attribute attribute) {
                if (attribute.getValueType() == null) {
                    return false;
                }
                return PARTITION_TYPES.contains(attribute.getValueType().toLowerCase());
            }
        });
    }


}
