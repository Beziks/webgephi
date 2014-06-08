package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.basic.DoublePropertyValue;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 23:24
 */
public class DoublePropertyInput extends AbstractPropertyInput<DoublePropertyValue> {
    public DoublePropertyInput(PropertyXml<DoublePropertyValue> property, FunctionSettingWidget owner) {
        super(property, owner);
    }

    @Override
    public AbstractField<?> createInput() {
        TextField f = new TextField(property.getName());
        f.setConverter(new StringToDoubleConverter());
        f.setNullRepresentation("Required");
        return f;
    }

    @Override
    public DoublePropertyValue getValue() {
        property.getValue().setValue((Double) input.getConvertedValue());
        return property.getValue();
    }

    @Override
    public void setValue(DoublePropertyValue value) {
        input.setConvertedValue(value.getValue());
    }


}
