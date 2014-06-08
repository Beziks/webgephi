package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.colorpicker.ColorChangeEvent;
import com.vaadin.ui.components.colorpicker.ColorChangeListener;
import cz.cokrtvac.webgephi.api.model.property.ColorPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 23:24
 */
public class ColorPropertyInput extends AbstractPropertyInput<ColorPropertyValue> {

    public ColorPropertyInput(final PropertyXml<ColorPropertyValue> property, FunctionSettingWidget owner) {
        super(property, owner);
        ColorPicker picker = new ColorPicker();
        picker.addColorChangeListener(new ColorChangeListener() {
            @Override
            public void colorChanged(ColorChangeEvent colorChangeEvent) {
                property.getValue().setValue(colorChangeEvent.getColor().getCSS().substring(1));
                setValue(property.getValue());
            }
        });
        try {
            picker.setColor(new Color(Integer.valueOf(getValue().getValue(), 16)));
        } catch (Exception e) {
            // Do nothing - white by default
        }

        layout.addComponent(picker);
        layout.setComponentAlignment(picker, Alignment.BOTTOM_LEFT);
    }

    @Override
    public AbstractField<?> createInput() {
        TextField f = new TextField(property.getName());
        return f;
    }

    @Override
    public ColorPropertyValue getValue() {
        property.getValue().setValue(((TextField) input).getValue());
        return property.getValue();
    }

    @Override
    public void setValue(ColorPropertyValue value) {
        ((TextField) input).setValue(value.getValue());
    }


}
