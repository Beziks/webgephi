package cz.cokrtvac.webgephi.clientapp.ui.graph_detail;

import com.vaadin.cdi.UIScoped;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import cz.cokrtvac.webgephi.clientapp.ui.Selected;
import org.slf4j.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 7. 4. 2014
 * Time: 23:10
 */
@UIScoped
public class StatisticsReportPanel extends CustomComponent {
    public static final String NO_REPORT_MESSAGE = "No report exists for this graph";

    @Inject
    private Logger log;

    @Inject
    private UserSession userSession;

    private GraphDetailXml currentGraph = null;

    private Label label;

    private VerticalLayout layout;

    public StatisticsReportPanel() {
        label = new Label(NO_REPORT_MESSAGE);
        label.setContentMode(ContentMode.HTML);
        layout = new VerticalLayout(label);
        layout.setMargin(true);
        label.setImmediate(true);
        setCompositionRoot(layout);
    }

    public void updateSelection(@Observes(notifyObserver = Reception.IF_EXISTS) @Selected GraphDetailXml graph) {
        if (currentGraph != null && graph.getId().equals(currentGraph.getId())) {
            // No update needed
            return;
        }

        log.debug("Updating graph detail " + graph);
        this.currentGraph = graph;

        try {
            if (updateGraphReport()) {
                setEnabled(true);
            }  else {
                setEnabled(false);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Notification.show("Update error", e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    private boolean updateGraphReport() {
        try {
            if (currentGraph.hasStatistics()) {
                String report = userSession.getWebgephiClient().getGraphStatisticReport(currentGraph.getId());
                label.setValue(report);
                return true;
            } else {
                label.setValue(NO_REPORT_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            log.error("Loading of REPORT failed: " + e.getMessage(), e);
            Notification.show("Loading of REPORT failed", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            return false;
        }
    }
}
