package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;
import cz.cokrtvac.webgephi.api.model.PropertyXml;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 23:24
 */
public class DoublePropertyInput extends AbstractPropertyInput<Double> {

    public DoublePropertyInput(PropertyXml<Double> property, FunctionSettingWidget owner) {
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
    public Double getValue() {
        return (Double) input.getConvertedValue();
    }

    @Override
    public void setValue(Double value) {
        input.setConvertedValue(value);
    }


}
