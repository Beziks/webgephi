package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;
import cz.cokrtvac.webgephi.api.model.PropertyXml;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 23:24
 */
public class StringPropertyInput extends AbstractPropertyInput<String> {

    public StringPropertyInput(PropertyXml<String> property, FunctionSettingWidget owner) {
        super(property, owner);
    }

    @Override
    public AbstractField<?> createInput() {
        TextField f = new TextField(property.getName());
        return f;
    }

    @Override
    public String getValue() {
        return ((TextField) input).getValue();
    }

    @Override
    public void setValue(String value) {
        ((TextField) input).setValue(value);
    }


}
