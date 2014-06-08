package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.ui.VerticalLayout;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.range.AttributeRangePropertyValue;
import cz.cokrtvac.webgephi.api.model.property.range.RangePropertyValue;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 2. 6. 2014
 * Time: 21:02
 */
public class AttributeRangePropertyInput extends AttributePropertyInput<AttributeRangePropertyValue> {
    private PropertyXml<RangePropertyValue> rangeTmpProperty;
    private RangePropertyInput rangeInput;

    public AttributeRangePropertyInput(PropertyXml<AttributeRangePropertyValue> property, FunctionSettingWidget owner) {
        super(property, owner);
    }

    @Override
    protected void afterCreate(VerticalLayout layout) {
        rangeTmpProperty = new PropertyXml<RangePropertyValue>("range-tmp", "Range", "Range of selected attribute", property.getValue().getRange());
    }

    @Override
    public boolean isValid() {
        return rangeInput != null && super.isValid() && rangeInput.isValid();
    }

    @Override
    protected void inputChanged(AttributeRangePropertyValue newVal) {
        try {
            if (rangeInput != null) {
                layout.removeComponent(rangeInput);
            }

            if (graph != null && property.getValue().getAttributeId() != null) {
                property.getValue().initRange(graph);
            }

            rangeInput = (RangePropertyInput) PropertyInputFactory.create(rangeTmpProperty, owner);
            layout.addComponent(rangeInput);
        } catch (Exception e) {
            log.error("Attribute range property could not be updated", e);
        }
    }
}
