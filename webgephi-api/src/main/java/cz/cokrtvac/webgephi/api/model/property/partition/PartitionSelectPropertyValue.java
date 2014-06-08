package cz.cokrtvac.webgephi.api.model.property.partition;

import cz.cokrtvac.webgephi.api.model.property.ListPropertyValue;

import javax.xml.bind.annotation.XmlElement;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 31. 5. 2014
 * Time: 23:57
 * <p/>
 * Combines Partition and list.
 * Enumeration of selected partitions of defined attribute
 */
public class PartitionSelectPropertyValue extends PartitionPropertyValue {
    public static final String TYPE = "partitionSelect";
    private ListPropertyValue partitionValues = new ListPropertyValue();

    public PartitionSelectPropertyValue() {
    }

    public PartitionSelectPropertyValue(String attributeId) {
        super(attributeId);
    }

    @XmlElement(name = "selected")
    public ListPropertyValue getPartitionValues() {
        return partitionValues;
    }

    public void setPartitionValues(ListPropertyValue partitionValues) {
        this.partitionValues = partitionValues;
    }
}
