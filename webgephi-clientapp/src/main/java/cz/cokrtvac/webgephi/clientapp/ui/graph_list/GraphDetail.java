package cz.cokrtvac.webgephi.clientapp.ui.graph_list;

import com.vaadin.cdi.UIScoped;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.client.ErrorHttpResponseException;
import cz.cokrtvac.webgephi.client.WebgephiClientException;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import cz.cokrtvac.webgephi.clientapp.ui.Selected;
import org.slf4j.Logger;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;
import java.text.SimpleDateFormat;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 6. 4. 2014
 * Time: 12:25
 */
@UIScoped
public class GraphDetail extends CustomComponent {
    @Inject
    private Logger log;

    @Inject
    private LazyQueryContainer graphsLazyDatasource;

    @Inject
    private UserSession userSession;

    @Inject
    @Selected
    private javax.enterprise.event.Event<GraphDetailXml> graphSelectedEvent;

    private GraphDetailXml currentGraph = null;
    private VerticalLayout layout;
    private Label label;
    private Table table;
    private BeanContainer<Long, GraphDetailXml> beanItemContainer;

    public GraphDetail() {
        layout = new VerticalLayout();
        setCompositionRoot(layout);
    }

    @PostConstruct
    public void init() {
        label = new Label("Select graph in 'All graphs' tab first");
        label.setContentMode(ContentMode.HTML);
        layout.addComponent(label);

        table = new Table("Graph history");
        table.setSelectable(true);
        table.setMultiSelect(false);
        table.setImmediate(true);
        table.setSizeFull();

        beanItemContainer = new BeanContainer<Long, GraphDetailXml>(GraphDetailXml.class);
        beanItemContainer.setBeanIdProperty("id");

        table.setContainerDataSource(beanItemContainer);
        table.setVisibleColumns(new Object[]{
                "id",
                "name",
                "created"
        });

        table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);

        table.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(final Property.ValueChangeEvent event) {
                Long itemId = (Long) event.getProperty().getValue();

                if (itemId == null) {
                    //deselect
                    return;
                }

                BeanItem<GraphDetailXml> item = (BeanItem<GraphDetailXml>) table.getItem(itemId);

                log.debug("Changed row selection  -> " + item.getBean().getId() + " " + table.getValue());
                graphSelectedEvent.fire(item.getBean());
            }
        });

        layout.addComponent(table);
    }

    private void updateGraphDetail() throws WebgephiClientException, ErrorHttpResponseException {
        String html = "<ul>" +
                "<li><span class='name'>ID: </span><span class='value'>" + currentGraph.getId() + "</span></li>" +
                "<li><span class='name'>Name: </span><span class='value'>" + currentGraph.getName() + "</span></li>" +
                "<li><span class='name'>Created: </span><span class='value'>" + new SimpleDateFormat("yyyy/MM/dd HH:mm").format(currentGraph.getCreated()) + "</span></li>" +
                "</ul>";
        label.setValue(html);

        // History
        beanItemContainer.removeAllItems();
        GraphDetailXml cur = currentGraph;
        while (cur != null) {
            beanItemContainer.addBean(cur);
            cur = cur.getParent();
            if (cur != null) {
                cur = userSession.getWebgephiClient().getGraph(cur.getId());
            }
        }
    }

    public void updateSelection(@Observes(notifyObserver = Reception.IF_EXISTS) @Selected GraphDetailXml graph) {
        if (currentGraph != null && graph.getId().equals(currentGraph.getId())) {
            // No update needed
            return;
        }

        log.debug("Updating graph detail " + graph);
        this.currentGraph = graph;

        try {
            updateGraphDetail();
            table.select(graph.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Notification.show("Update error", e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }
}
