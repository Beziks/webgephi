package cz.cokrtvac.webgephi.clientapp.ui.graph_list;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import cz.cokrtvac.webgephi.client.ErrorHttpResponseException;
import cz.cokrtvac.webgephi.client.WebgephiClientException;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 6. 4. 2014
 * Time: 12:23
 */
public class GraphListTabPanel extends CustomComponent {
    private TabSheet tabSheet;

    @Inject
    private Logger log;

    @Inject
    private UserSession userSession;

    @Inject
    private GraphListTable graphListTable;

    @Inject
    private GraphDetail graphDetail;

    public GraphListTabPanel() {
        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        setSizeFull();
        setCompositionRoot(tabSheet);
    }

    @PostConstruct
    private void init() throws WebgephiClientException, ErrorHttpResponseException {
        tabSheet.addTab(graphListTable, "All graphs");
        tabSheet.addTab(graphDetail, "Current graph");
    }
}
