package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.CheckBox;
import cz.cokrtvac.webgephi.api.model.PropertyXml;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 23:24
 */
public class BooleanPropertyInput extends AbstractPropertyInput<Boolean> {

    public BooleanPropertyInput(PropertyXml<Boolean> property, FunctionSettingWidget owner) {
        super(property, owner);
    }

    @Override
    public AbstractField<?> createInput() {
        CheckBox checkBox = new CheckBox();
        return checkBox;
    }

    @Override
    public Boolean getValue() {
        return ((CheckBox) input).getValue();
    }

    @Override
    public void setValue(Boolean value) {
        ((CheckBox) input).setValue(value);
    }
}
