package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.basic.NumberPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.range.RangePropertyValue;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 23:24
 */
public class RangePropertyInput<I extends NumberPropertyValue> extends CustomComponent implements PropertyInput<RangePropertyValue> {
    protected Logger log = LoggerFactory.getLogger(getClass());

    protected PropertyXml<RangePropertyValue> property;
    protected PropertyInput<I> fromInput;
    protected PropertyInput<I> toInput;
    protected FunctionSettingWidget owner;
    protected HorizontalLayout layout;
    protected VerticalLayout mainLayout;

    public RangePropertyInput(final PropertyXml<RangePropertyValue> property, final FunctionSettingWidget owner) {
        this.property = property;
        this.owner = owner;

        PropertyXml<? extends NumberPropertyValue> fromTmpProp = new PropertyXml<NumberPropertyValue>("from-tmp", "From", "Lower bound of range", property.getValue().getFrom());
        fromInput = (PropertyInput<I>) PropertyInputFactory.create(fromTmpProp, owner);

        PropertyXml<? extends NumberPropertyValue> toTmpProp = new PropertyXml<NumberPropertyValue>("to-tmp", "To", "Upper bound of range", property.getValue().getTo());
        toInput = (PropertyInput<I>) PropertyInputFactory.create(toTmpProp, owner);

        layout = new HorizontalLayout(fromInput, toInput);

        Label label = new Label(property.getName() + " (" + property.getValue().getType() + ")");
        label.setDescription(property.getDescription());
        layout.setSpacing(true);
        setWidth(100, Unit.PERCENTAGE);
        layout.setWidth(100, Unit.PERCENTAGE);
        mainLayout = new VerticalLayout(label, layout);
        mainLayout.setWidth(100, Unit.PERCENTAGE);
        setCompositionRoot(mainLayout);
    }

    @Override
    public RangePropertyValue getValue() {
        return property.getValue();
    }

    @Override
    public void setValue(RangePropertyValue value) {
        fromInput.setValue((I) value.getFrom());
    }

    @Override
    public boolean isValid() {
        return fromInput.isValid() && toInput.isValid();
    }

    @Override
    public void graphChanged(GraphDetailXml graph, UserSession userSession) {
    }
}
