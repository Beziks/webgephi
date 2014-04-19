package cz.cokrtvac.webgephi.clientapp.ui.functions;

import com.vaadin.ui.Layout;
import cz.cokrtvac.webgephi.api.model.AbstractFunction;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.client.ErrorHttpResponseException;
import cz.cokrtvac.webgephi.client.WebgephiClientException;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import org.vaadin.risto.stepper.IntStepper;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 11. 4. 2014
 * Time: 0:01
 */
public class LayoutSettingWidget extends FunctionSettingWidget {
    protected IntStepper repeat;

    public LayoutSettingWidget(AbstractFunction function, UserSession userSession) {
        super(function, userSession);
    }

    @Override
    protected Layout createHeader() {
        Layout l = super.createHeader();
        repeat = new IntStepper("Repeat execution");
        repeat.setValue(1);
        repeat.setStepAmount(1);
        repeat.setMaxValue(10);
        repeat.setMinValue(1);
        repeat.setManualInputAllowed(false);
        l.addComponent(repeat);
        return l;
    }

    @Override
    protected GraphDetailXml execute() throws WebgephiClientException, ErrorHttpResponseException {
        return userSession.getWebgephiClient().applyLayoutFunction(currentGraph.getId(), (LayoutXml) function, graphName.getValue(), repeat.getValue());
    }
}
