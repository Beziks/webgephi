package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.basic.BasicPropertyValue;
import cz.cokrtvac.webgephi.api.model.property.partition.PartitionSelectPropertyValue;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 23:24
 */
public class PartitionSelectPropertyInput<T extends PartitionSelectPropertyValue> extends AttributePropertyInput<T> {
    private VerticalLayout partsLayout;
    private T propertyValue;
    private String lastAttribute;
    private Property.ValueChangeListener partChangedListener = new Property.ValueChangeListener() {
        @Override
        public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
            List<BasicPropertyValue> selected = new ArrayList<BasicPropertyValue>();
            for (int i = 0; i < partsLayout.getComponentCount(); i++) {
                PartComponent component = (PartComponent) partsLayout.getComponent(i);
                BasicPropertyValue s = component.getPart();
                if (s != null) {
                    selected.add(s);
                }
            }
            getValue().getPartitionValues().setValues(selected);
        }
    };

    public PartitionSelectPropertyInput(PropertyXml<T> property, FunctionSettingWidget owner) {
        super(property, owner);
    }

    @Override
    protected void afterCreate(VerticalLayout layout) {
        partsLayout = new VerticalLayout();
        propertyValue = property.getValue();
        layout.addComponent(partsLayout);
    }

    @Override
    public AbstractField<?> createInput() {
        return super.createInput();
    }

    @Override
    public T getValue() {
        return super.getValue();
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
    }

    protected void inputChanged(T newVal) {
        try {
            if (graph == null) {
                return;
            }

            if (newVal.getAttributeId() == null) {
                partsLayout.removeAllComponents();
                propertyValue.getPartitionValues().getValues().clear();
                lastAttribute = newVal.getAttributeId();
                return;
            }

            if (newVal.getAttributeId().equals(lastAttribute)) {
                return;
            }

            partsLayout.removeAllComponents();
            for (BasicPropertyValue item : propertyValue.getAllAttributeValues(graph).getValues()) {
                partsLayout.addComponent(new PartComponent(item, partChangedListener));
            }

            lastAttribute = newVal.getAttributeId();
        } catch (TransformerException e) {
            log.error("Error during updating Partition select", e);
        }
    }

    @Override
    public void graphChanged(GraphDetailXml graph, UserSession userSession) {
        super.graphChanged(graph, userSession);
        lastAttribute = null;
        setValue(getValue());
    }

    public static class PartComponent extends CustomComponent {
        private BasicPropertyValue part;
        private CheckBox checkbox;

        public BasicPropertyValue getPart() {
            if (checkbox.getValue()) {
                return part;
            }
            return null;
        }

        public PartComponent(BasicPropertyValue part, Property.ValueChangeListener valueChangeListener) {
            this.part = part;
            this.checkbox = new CheckBox(part.getValue().toString());
            this.checkbox.addValueChangeListener(valueChangeListener);
            setCompositionRoot(this.checkbox);
        }
    }
}
