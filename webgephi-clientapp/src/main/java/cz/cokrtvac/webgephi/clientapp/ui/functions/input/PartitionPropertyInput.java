package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.ui.AbstractField;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.partition.PartitionPropertyValue;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 23:24
 */
public class PartitionPropertyInput<T extends PartitionPropertyValue> extends AttributePropertyInput<T> {

    public PartitionPropertyInput(PropertyXml<T> property, FunctionSettingWidget owner) {
        super(property, owner);
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

    @Override
    public void graphChanged(GraphDetailXml graph, UserSession userSession) {
        super.graphChanged(graph, userSession);
    }
}
