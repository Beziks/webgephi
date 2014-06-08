package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.ui.VerticalLayout;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.basic.BasicPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.range.AttributeEqualsPropertyValue;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 2. 6. 2014
 * Time: 21:02
 */
public class AttributeEqualsPropertyInput extends AttributePropertyInput<AttributeEqualsPropertyValue> {
    private PropertyXml<BasicPropertyValue> matchTmpProperty;
    private PropertyInput matchInput;

    public AttributeEqualsPropertyInput(PropertyXml<AttributeEqualsPropertyValue> property, FunctionSettingWidget owner) {
        super(property, owner);
    }

    @Override
    protected void afterCreate(VerticalLayout layout) {
    }

    @Override
    public boolean isValid() {
        return matchInput != null && super.isValid() && matchInput.isValid();
    }

    @Override
    protected void inputChanged(AttributeEqualsPropertyValue newVal) {
        try {
            if (matchInput != null) {
                layout.removeComponent(matchInput);
            }

            if (graph != null && property.getValue().getAttributeId() != null) {
                property.getValue().initValue(graph);
                matchTmpProperty = new PropertyXml<BasicPropertyValue>("value-tmp", "Match", "Attribute have to match this value", property.getValue().getAttributeValue());
                matchInput = PropertyInputFactory.create(matchTmpProperty, owner);
                layout.addComponent(matchInput);
            }
        } catch (Exception e) {
            log.error("Attribute equals property could not be updated", e);
        }
    }
}
