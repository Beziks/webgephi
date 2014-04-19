package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;
import cz.cokrtvac.webgephi.api.model.PropertyXml;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 23:24
 */
public class IntegerPropertyInput extends AbstractPropertyInput<Integer> {

    public IntegerPropertyInput(PropertyXml<Integer> property, FunctionSettingWidget owner) {
        super(property, owner);
    }

    @Override
    public AbstractField<?> createInput() {
        TextField f = new TextField(property.getName());
        f.setConverter(new StringToIntegerConverter());
        f.setNullRepresentation("Required");
        return f;
    }

    @Override
    public Integer getValue() {
        return (Integer) input.getConvertedValue();
    }

    @Override
    public void setValue(Integer value) {
        input.setConvertedValue(value);
    }


}
