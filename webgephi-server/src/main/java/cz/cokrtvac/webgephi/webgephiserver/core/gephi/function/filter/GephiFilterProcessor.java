package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter;

import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.FunctionProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.converter.FilterCustomConverter;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import org.gephi.filters.AbstractQueryImpl;
import org.gephi.filters.FilterProcessor;
import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Query;
import org.gephi.filters.spi.Filter;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.openide.util.Lookup;
import org.slf4j.Logger;

import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 9.6.13
 * Time: 18:10
 */
public class GephiFilterProcessor extends FunctionProcessor<FilterWrapper, FilterXml> {

    @Inject
    private Logger log;

    @Inject
    private FiltersPool filtersPool;

    public GephiFilterProcessor() {
    }

    public final FilterWrapper process(WorkspaceWrapper workspaceWrapper, FilterXml xml, Integer repeat) {
        FilterWrapper function = null;
        try {
            function = FilterCustomConverter.Converters.convert(xml, filtersPool, workspaceWrapper);
        } catch (ValidationException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return process(workspaceWrapper, function, xml, repeat);
    }

    /**
     * Apply filter function on graph
     *
     * @param workspaceWrapper - workspace with graph
     * @param function
     * @return
     */
    @Override
    public FilterWrapper process(WorkspaceWrapper workspaceWrapper, FilterWrapper function, FilterXml xml, Integer repeat) {
        try {
            FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
            Filter filter = function.createFilter();
            Query query = filterController.createQuery(filter);

            FilterProcessor processor = new FilterProcessor();
            GraphModel graphModel = workspaceWrapper.getGraphModel();
            Graph result = processor.process((AbstractQueryImpl) query, graphModel);
            workspaceWrapper.getGraphModel().setVisibleView(result.getView());
            return function;
        } catch (Exception e) {
            throw new IllegalArgumentException("Filter function could not be applied: " + e.getMessage(), e);
        }
    }

    @Override
    protected FilterWrapper createNew(FilterXml xml) {
        return filtersPool.createNew(xml);
    }
}
