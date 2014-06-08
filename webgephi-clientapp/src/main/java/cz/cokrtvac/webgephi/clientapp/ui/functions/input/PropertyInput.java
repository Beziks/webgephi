package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.ui.Component;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.property.PropertyValue;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 2. 6. 2014
 * Time: 18:34
 */
public interface PropertyInput<T extends PropertyValue> extends Component {
    public abstract T getValue();

    public abstract void setValue(T value);

    public boolean isValid();

    public void graphChanged(GraphDetailXml graph, UserSession userSession);
}
