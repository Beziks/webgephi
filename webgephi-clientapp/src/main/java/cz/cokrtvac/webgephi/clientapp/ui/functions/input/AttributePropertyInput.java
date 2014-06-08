package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.api.model.property.attribute.AttributePropertyValue;
import cz.cokrtvac.webgephi.api.util.XmlFastUtil;
import cz.cokrtvac.webgephi.client.ErrorHttpResponseException;
import cz.cokrtvac.webgephi.client.WebgephiClientException;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;
import org.w3c.dom.Document;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 23:24
 */
public class AttributePropertyInput<T extends AttributePropertyValue> extends AbstractPropertyInput<T> {
    protected Document graph;

    public AttributePropertyInput(PropertyXml<T> property, FunctionSettingWidget owner) {
        super(property, owner);
    }

    @Override
    public AbstractField<?> createInput() {
        ComboBox f = new ComboBox();
        f.setTextInputAllowed(false);
        f.setNullSelectionAllowed(false);

        if (owner.getUserSession().getCurrentGraph() != null) {
            graphChanged(owner.getUserSession().getCurrentGraph(), owner.getUserSession());
        }
        return f;
    }

    @Override
    public T getValue() {
        if (input != null) {
            property.getValue().setAttributeId((String) ((ComboBox) input).getValue());
        }
        return property.getValue();
    }

    @Override
    public void setValue(T value) {
        if (input != null) {
            ((ComboBox) input).setValue(value.getAttributeId());
        }
    }

    @Override
    public void graphChanged(GraphDetailXml graph, UserSession userSession) {
        if (input == null) {
            return;
        }
        String last = getValue().getAttributeId();
        ComboBox c = (ComboBox) input;
        c.removeAllItems();
        String gexf = null;
        try {
            gexf = userSession.getWebgephiClient().getGraphAsGexf(graph.getId());
        } catch (ErrorHttpResponseException e) {
            log.error("Cannot load GEXF format: " + e.getMessage(), e);
            Notification.show("Cannot load GEXF format", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            return;
        } catch (WebgephiClientException e) {
            log.error("Cannot load GEXF format: " + e.getMessage(), e);
            Notification.show("Cannot load GEXF format", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            return;
        }
        try {
            Document doc = XmlFastUtil.lsDeSerializeDom(gexf, false);
            this.graph = doc;

            String first = null;
            for (AttributePropertyValue.Attribute a : property.getValue().getPossibleAttributes(doc)) {
                c.addItem(a.getId());
                if (first == null) {
                    first = a.getId();
                }
            }
            c.setValue(last);
            if (c.getValue() == null) {
                c.setValue(first);
            }
        } catch (Exception e) {
            log.error("Cannot select attribute values from GEXF fromat: " + e.getMessage(), e);
            Notification.show("Cannot select attribute values from GEXF fromat", e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }
}
