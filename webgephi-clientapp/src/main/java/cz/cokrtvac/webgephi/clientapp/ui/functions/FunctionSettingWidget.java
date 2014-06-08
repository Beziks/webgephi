package cz.cokrtvac.webgephi.clientapp.ui.functions;

import com.vaadin.ui.*;
import cz.cokrtvac.webgephi.api.model.AbstractFunctionXml;
import cz.cokrtvac.webgephi.api.model.GraphFunctionXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.property.PropertyXml;
import cz.cokrtvac.webgephi.client.ErrorHttpResponseException;
import cz.cokrtvac.webgephi.client.WebgephiClientException;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import cz.cokrtvac.webgephi.clientapp.ui.functions.input.AbstractPropertyInput;
import cz.cokrtvac.webgephi.clientapp.ui.functions.input.PropertyInput;
import cz.cokrtvac.webgephi.clientapp.ui.functions.input.PropertyInputFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 22:04
 */
public abstract class FunctionSettingWidget extends CustomComponent {
    protected Logger log = LoggerFactory.getLogger(getClass());

    protected UserSession userSession;
    protected AbstractFunctionXml function;
    protected GraphDetailXml currentGraph;

    List<PropertyInput<?>> inputs = new ArrayList<PropertyInput<?>>();

    protected TextField graphName;
    private Button executeButton;

    public FunctionSettingWidget(final AbstractFunctionXml function, final UserSession userSession) {
        this.userSession = userSession;
        this.function = function;
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(false);
        setCompositionRoot(layout);
        setEnabled(false);

        Component header = createHeader();
        if (header != null) {
            layout.addComponent(header);
        }

        VerticalLayout propsLayout = new VerticalLayout();
        propsLayout.setSpacing(true);
        propsLayout.setMargin(true);
        for (PropertyXml<?> p : function.getProperties()) {
            PropertyInput<?> input = PropertyInputFactory.create(p, this);
            if (input != null) {
                propsLayout.addComponent(input);
                inputs.add(input);
            }
        }
        propsLayout.setStyleName("functionProperties");
        layout.addComponent(propsLayout);

        executeButton = new Button("Execute", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    log.info("Apply function: " + function.toString());
                    GraphDetailXml result = execute();
                    userSession.refreshGraphList();
                    userSession.getGraphSelectedEvent().fire(result);
                } catch (ErrorHttpResponseException e) {
                    log.warn(e.getMessage(), e);
                    Notification.show("Invalid input", e.getMessage(), Notification.Type.WARNING_MESSAGE);
                } catch (WebgephiClientException e) {
                    log.error(e.getMessage(), e);
                    Notification.show("Internal error", e.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });

        executeButton.setDescription("Apply function on current graph. New graph will be created with name specified in 'Graph name' input");
        propsLayout.addComponent(executeButton);
        inputChanged(null);
    }

    protected GraphDetailXml execute() throws WebgephiClientException, ErrorHttpResponseException {
        return userSession.getWebgephiClient().applyFunction(currentGraph.getId(), new GraphFunctionXml(function), graphName.getValue());
    }

    protected Layout createHeader() {
        CssLayout l = new CssLayout();
        l.setStyleName("functionHeader");
        if (function.getDescription() != null) {
            Label desc = new Label(function.getDescription());
            desc.setStyleName("functionDescription");
            l.addComponent(desc);
        }
        graphName = new TextField("Graph name", "Enter new graph name...");
        graphName.setWidth(100, Unit.PERCENTAGE);
        graphName.setMaxLength(200);
        l.addComponent(graphName);
        return l;
    }

    public void graphChanged(GraphDetailXml graph, UserSession userSession) {
        this.currentGraph = graph;
        setEnabled(graph != null);

        String name = graph.getName();
        int ii = name.lastIndexOf("#");
        if (ii < 0) {
            name += "#1";
        } else {
            String num = name.substring(ii + 1);
            try {
                Integer n = Integer.valueOf(num);
                name = name.substring(0, ii + 1) + ++n;
            } catch (Exception e) {
                name += "#1";
            }
        }

        if (name.length() > 200) {
            name = name.substring(0, 200);
        }

        graphName.setValue(name);

        for (PropertyInput<?> i : inputs) {
            i.graphChanged(graph, userSession);
        }

    }

    public void inputChanged(AbstractPropertyInput<?> source) {
        if (executeButton == null) {
            return;
        }
        for (PropertyInput<?> i : inputs) {
            if (!i.isValid()) {
                executeButton.setEnabled(false);
                return;
            }
        }
        executeButton.setEnabled(true);
    }

    public UserSession getUserSession() {
        return userSession;
    }
}
