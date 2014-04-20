package cz.cokrtvac.webgephi.clientapp.ui.functions.input;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import cz.cokrtvac.webgephi.api.model.PropertyXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.util.XmlFastUtil;
import cz.cokrtvac.webgephi.client.ErrorHttpResponseException;
import cz.cokrtvac.webgephi.client.WebgephiClientException;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import cz.cokrtvac.webgephi.clientapp.ui.functions.FunctionSettingWidget;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 23:24
 */
public class SelectAttributePropertyInput extends AbstractPropertyInput<String> {

    public SelectAttributePropertyInput(PropertyXml<String> property, FunctionSettingWidget owner) {
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
    public String getValue() {
        return (String) ((ComboBox) input).getValue();
    }

    @Override
    public void setValue(String value) {
        ((ComboBox) input).setValue(value);
    }

    @Override
    public void graphChanged(GraphDetailXml graph, UserSession userSession) {
        if (input == null) {
            return;
        }
        String last = getValue();
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
            NodeList nl = XPathAPI.selectNodeList(doc.getDocumentElement(), "/gexf/graph/attributes[@class='node']/attribute/@id");
            String first = null;
            for (int i = 0; i < nl.getLength(); i++) {
                Attr a = (Attr) nl.item(i);
                c.addItem(a.getValue());

                if (first == null) {
                    first = a.getValue();
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
