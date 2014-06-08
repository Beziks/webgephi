package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import cz.cokrtvac.webgephi.api.model.property.ColorPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.PropertyValue;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.attribute.AttributePropertyValue;
import cz.cokrtvac.webgephi.api.model.property.basic.*;
import cz.cokrtvac.webgephi.api.model.property.partition.PartitionPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.partition.PartitionSelectPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.range.AttributeEqualsPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.range.AttributeRangePropertyValue;
import cz.cokrtvac.webgephi.api.model.property.range.RangePropertyValue;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 2. 6. 2014
 * Time: 18:42
 */
public class PropertyInputFactory {
    public static final Logger LOG = LoggerFactory.getLogger(PropertyInput.class);

    /**
     * Factory method
     *
     * @param property
     * @param <T>
     * @return
     */
    public static <T extends PropertyValue> PropertyInput<T> create(PropertyXml<T> property, FunctionSettingWidget owner) {
        if (property.getValue() instanceof AttributeEqualsPropertyValue) {
            return (PropertyInput<T>) new AttributeEqualsPropertyInput((PropertyXml<AttributeEqualsPropertyValue>) property, owner);
        }
        if (property.getValue() instanceof AttributeRangePropertyValue) {
            return (PropertyInput<T>) new AttributeRangePropertyInput((PropertyXml<AttributeRangePropertyValue>) property, owner);
        }
        if (property.getValue() instanceof DoublePropertyValue) {
            return (PropertyInput<T>) new DoublePropertyInput((PropertyXml<DoublePropertyValue>) property, owner);
        }
        if (property.getValue() instanceof PartitionSelectPropertyValue) {
            return (PropertyInput<T>) new PartitionSelectPropertyInput((PropertyXml<PartitionSelectPropertyValue>) property, owner);
        }
        if (property.getValue() instanceof PartitionPropertyValue) {
            return (PropertyInput<T>) new PartitionPropertyInput((PropertyXml<PartitionPropertyValue>) property, owner);
        }
        if (property.getValue() instanceof AttributePropertyValue) {
            return (PropertyInput<T>) new AttributePropertyInput((PropertyXml<AttributePropertyValue>) property, owner);
        }
        if (property.getValue() instanceof ColorPropertyValue) {
            return (PropertyInput<T>) new ColorPropertyInput((PropertyXml<ColorPropertyValue>) property, owner);
        }
        if (property.getValue() instanceof StringPropertyValue) {
            return (PropertyInput<T>) new StringPropertyInput((PropertyXml<StringPropertyValue>) property, owner);
        }
        if (property.getValue() instanceof BooleanPropertyValue) {
            return (PropertyInput<T>) new BooleanPropertyInput((PropertyXml<BooleanPropertyValue>) property, owner);
        }
        if (property.getValue() instanceof IntegerPropertyValue) {
            return (PropertyInput<T>) new IntegerPropertyInput((PropertyXml<IntegerPropertyValue>) property, owner);
        }
        if (property.getValue() instanceof FloatPropertyValue) {
            return (PropertyInput<T>) new FloatPropertyInput((PropertyXml<FloatPropertyValue>) property, owner);
        }
        if (property.getValue() instanceof RangePropertyValue) {
            return (PropertyInput<T>) new RangePropertyInput((PropertyXml<RangePropertyValue>) property, owner);
        }

        LOG.warn("No implementation of property for type " + property.getValue().getClass());
        return null;
    }
}
