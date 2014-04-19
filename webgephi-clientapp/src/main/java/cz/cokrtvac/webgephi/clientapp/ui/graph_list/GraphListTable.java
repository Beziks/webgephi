package cz.cokrtvac.webgephi.clientapp.ui.graph_list;

import com.vaadin.cdi.UIScoped;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.clientapp.ui.Selected;
import org.slf4j.Logger;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 6. 4. 2014
 * Time: 12:25
 */
@UIScoped
public class GraphListTable extends CustomComponent {
    @Inject
    private Logger log;

    @Inject
    private LazyQueryContainer graphsLazyDatasource;

    @Inject
    @Selected
    private javax.enterprise.event.Event<GraphDetailXml> graphSelectedEvent;

    private GraphDetailXml currentGraph = null;

    private Table table;

    public GraphListTable() {
        table = new Table();
        setSizeFull();
        table.setSizeFull();
        setCompositionRoot(table);
    }

    @PostConstruct
    public void init() {
        table.setSelectable(true);
        table.setMultiSelect(false);
        table.setImmediate(true);

        table.setContainerDataSource(graphsLazyDatasource);
        //table.setPageLength(20);

        table.setVisibleColumns(new Object[]{
                "id",
                "name",
                "created"
        });

        table.setColumnReorderingAllowed(false);
        table.setColumnCollapsingAllowed(false);

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
    }

    public void updateSelection(@Observes(notifyObserver = Reception.IF_EXISTS) @Selected GraphDetailXml graph) {
        if (currentGraph != null && currentGraph.getId().equals(graph.getId())) {
            // No need for update
            log.debug("No need for update, same node selected: " + currentGraph.getId() + "x" + graph.getId());
            return;
        }

        log.debug("Selecting row with graph id " + graph.getId());
        currentGraph = graph;
        table.select(graph.getId());
    }
}
