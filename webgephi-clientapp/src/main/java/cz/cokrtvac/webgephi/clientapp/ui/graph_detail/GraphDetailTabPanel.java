package cz.cokrtvac.webgephi.clientapp.ui.graph_detail;

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.client.ErrorHttpResponseException;
import cz.cokrtvac.webgephi.client.WebgephiClientException;
import cz.cokrtvac.webgephi.clientapp.ui.Selected;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 6. 4. 2014
 * Time: 12:23
 */
@UIScoped
public class GraphDetailTabPanel extends CustomComponent {
    private TabSheet tabSheet;

    @Inject
    private Logger log;

    @Inject
    private GraphSvgPanel graphSvgPanel;

    @Inject
    private GraphGexfPanel graphGexfPanel;

    @Inject
    private StatisticsReportPanel statisticsReportPanel;

    public GraphDetailTabPanel() {
        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        setSizeFull();
        setCompositionRoot(tabSheet);
    }

    @PostConstruct
    private void init() throws WebgephiClientException, ErrorHttpResponseException {
        tabSheet.addTab(graphSvgPanel, "SVG");
        tabSheet.addTab(graphGexfPanel, "GEXF");
        tabSheet.addTab(statisticsReportPanel, "Statistics Report");
        tabSheet.getTab(statisticsReportPanel).setEnabled(false);
       /* tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                if (tabSheet.getSelectedTab() == graphGexfPanel) {
                   graphGexfPanel.refresh();
                }
            }
        });  */
    }

    public void updateSelection(@Observes(notifyObserver = Reception.IF_EXISTS) @Selected GraphDetailXml graph) {
        if (graph == null || !graph.hasStatistics()) {
            tabSheet.getTab(statisticsReportPanel).setEnabled(false);
        } else {
            tabSheet.getTab(statisticsReportPanel).setEnabled(true);
        }
    }
}
