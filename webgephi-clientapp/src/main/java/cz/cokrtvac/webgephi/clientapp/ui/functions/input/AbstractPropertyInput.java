package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.property.PropertyValue;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 22:04
 */
public abstract class AbstractPropertyInput<T extends PropertyValue> extends CustomComponent implements PropertyInput<T> {
    protected Logger log = LoggerFactory.getLogger(getClass());

    protected PropertyXml<T> property;
    protected AbstractField<?> input;
    protected FunctionSettingWidget owner;
    protected VerticalLayout layout;

    public AbstractPropertyInput(final PropertyXml<T> property, final FunctionSettingWidget owner) {
        layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setWidth(100, Unit.PERCENTAGE);
        setWidth(100, Unit.PERCENTAGE);
        this.owner = owner;
        this.property = property;
        input = createInput();
        input.setCaption(property.getName() + " (" + property.getValue().getType() + ")");

        input.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (input.isValid()) {
                    T newVal = getValue();
                    log.debug("Value changed to: " + newVal);
                    property.setValue(newVal);
                    inputChanged(newVal);
                }
                owner.inputChanged(AbstractPropertyInput.this);
            }
        });

        input.setImmediate(true);
        input.setRequired(true);
        input.setValidationVisible(true);
        input.setRequiredError("Value is required");
        input.setWidth(100, Unit.PERCENTAGE);

        input.setDescription(property.getDescription());

        layout.addComponent(input);
        afterCreate(layout);

        setCompositionRoot(layout);

        setValue(property.getValue());
    }

    protected void inputChanged(T newVal) {
    }

    public AbstractField<?> getInput() {
        return input;
    }

    public abstract AbstractField<?> createInput();

    public abstract T getValue();

    public abstract void setValue(T value);

    public boolean isValid() {
        return input.isValid();
    }

    public void graphChanged(GraphDetailXml graph, UserSession userSession) {
    }

    protected void afterCreate(VerticalLayout layout) {
    }
}
