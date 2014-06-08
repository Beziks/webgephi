package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.basic.StringPropertyValue;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 23:24
 */
public class StringPropertyInput extends AbstractPropertyInput<StringPropertyValue> {

    public StringPropertyInput(PropertyXml<StringPropertyValue> property, FunctionSettingWidget owner) {
        super(property, owner);
    }

    @Override
    public AbstractField<?> createInput() {
        TextField f = new TextField(property.getName());
        return f;
    }

    @Override
    public StringPropertyValue getValue() {
        property.getValue().setValue(((TextField) input).getValue());
        return property.getValue();
    }

    @Override
    public void setValue(StringPropertyValue value) {
        ((TextField) input).setValue(value.getValue());
    }


}
