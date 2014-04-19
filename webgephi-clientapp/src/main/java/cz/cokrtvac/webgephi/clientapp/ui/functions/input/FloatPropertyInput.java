package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.data.util.converter.StringToFloatConverter;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;
import cz.cokrtvac.webgephi.api.model.PropertyXml;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 23:24
 */
public class FloatPropertyInput extends AbstractPropertyInput<Float> {

    public FloatPropertyInput(PropertyXml<Float> property, FunctionSettingWidget owner) {
        super(property, owner);
    }

    @Override
    public AbstractField<?> createInput() {
        TextField f = new TextField(property.getName());
        f.setConverter(new StringToFloatConverter());
        f.setNullRepresentation("Required");
        return f;
    }

    @Override
    public Float getValue() {
        return (Float) input.getConvertedValue();
    }

    @Override
    public void setValue(Float value) {
        input.setConvertedValue(value);
    }


}