package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.CheckBox;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.basic.BooleanPropertyValue;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 23:24
 */
public class BooleanPropertyInput extends AbstractPropertyInput<BooleanPropertyValue> {

    public BooleanPropertyInput(PropertyXml<BooleanPropertyValue> property, FunctionSettingWidget owner) {
        super(property, owner);
    }

    @Override
    public AbstractField<?> createInput() {
        CheckBox checkBox = new CheckBox();
        return checkBox;
    }

    @Override
    public BooleanPropertyValue getValue() {
        property.getValue().setValue(((CheckBox) input).getValue());
        return property.getValue();
    }

    @Override
    public void setValue(BooleanPropertyValue value) {
        ((CheckBox) input).setValue(value.getValue());
    }
}
