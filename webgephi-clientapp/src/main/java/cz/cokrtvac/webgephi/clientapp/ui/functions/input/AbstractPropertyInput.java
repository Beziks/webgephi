package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import cz.cokrtvac.webgephi.api.model.PropertyXml;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 22:04
 */
public abstract class AbstractPropertyInput<T> extends CustomComponent {
    protected Logger log = LoggerFactory.getLogger(getClass());
    protected static Logger LOG = LoggerFactory.getLogger(AbstractPropertyInput.class);

    /**
     * Factory method
     *
     * @param property
     * @param <T>
     * @return
     */
    public static <T> AbstractPropertyInput<T> create(PropertyXml<T> property, FunctionSettingWidget owner){
        if(property.getValue() instanceof Double) {
            return (AbstractPropertyInput<T>) new DoublePropertyInput((PropertyXml<Double>) property, owner);
        }
        if(property.getValue() instanceof String){
            return (AbstractPropertyInput<T>) new StringPropertyInput((PropertyXml<String>) property, owner);
        }
        if(property.getValue() instanceof Boolean){
            return (AbstractPropertyInput<T>) new BooleanPropertyInput((PropertyXml<Boolean>) property, owner);
        }
        if(property.getValue() instanceof  Integer){
            return (AbstractPropertyInput<T>) new IntegerPropertyInput((PropertyXml<Integer>) property, owner);
        }
        if(property.getValue() instanceof Float){
            return (AbstractPropertyInput<T>) new FloatPropertyInput((PropertyXml<Float>) property, owner);
        }

        LOG.warn("No implementation of property for type " + property.getValue().getClass());
        return null;
    }

    protected PropertyXml<?> property;
    protected AbstractField<?> input;
    protected  FunctionSettingWidget owner;

    public AbstractPropertyInput(final PropertyXml<T> property, final FunctionSettingWidget owner) {
        HorizontalLayout layout = new HorizontalLayout();
        this.owner = owner;
        this.property = property;
        input = createInput();
        input.setCaption(property.getName() + " (" + property.getValue().getClass().getSimpleName() + ")");

        input.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(input.isValid()) {
                    T newVal = getValue();
                    log.debug("Value changed to: " + newVal);
                    property.setValue(newVal);
                }
                owner.inputChanged(AbstractPropertyInput.this);
            }
        });

        input.setImmediate(true);
        input.setRequired(true);
        input.setValidationVisible(true);
        input.setRequiredError("Value is required");

        input.setDescription(property.getDescription());

        setValue(property.getValue());

        layout.addComponent(input);
        setCompositionRoot(layout);
    }

    public AbstractField<?> getInput() {
        return input;
    }

    public abstract AbstractField<?> createInput();
    public abstract T getValue();
    public abstract void setValue(T value);

    public boolean isValid(){
        return input.isValid();
    }
}
