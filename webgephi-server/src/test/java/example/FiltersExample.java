package example;

import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.filters.AbstractQueryImpl;
import org.gephi.filters.FilterProcessor;
import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Query;
import org.gephi.filters.plugin.graph.GiantComponentBuilder;
import org.gephi.filters.spi.Filter;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.openide.util.Lookup;

import java.lang.reflect.InvocationTargetException;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 1. 5. 2014
 * Time: 12:24
 */
public class FiltersExample {
    public void example() throws InvocationTargetException, IllegalAccessException {
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
        FilterController filterController = null;


// 31 - 40 ================================================================================
// Create builder and filter
        GiantComponentBuilder builder = new GiantComponentBuilder();
        Filter filter = builder.getFilter();
// Create query from filter
        Query query = filterController.createQuery(filter);
// Apply filter
        FilterProcessor processor = new FilterProcessor();
        Graph result = processor.process((AbstractQueryImpl) query, graphModel);
// Set result view as visible
        graphModel.setVisibleView(result.getView());
// ================================================================================
    }
}
