package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter;

import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.api.model.filter.FiltersXml;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.GephiScanner;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.FunctionsPool;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.converter.FilterCustomConverter;
import org.gephi.filters.spi.FilterBuilder;
import org.gephi.filters.spi.Operator;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.List;

/**
 * Provides info about all available layout functions in application.
 * Runs after deploy.
 * <p/>
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 15:18
 */
@Singleton
@Startup
public class FiltersPool extends FunctionsPool<FilterWrapper, FilterXml, FiltersXml> {

    @Inject
    private Logger log;

    @Inject
    private GephiScanner gephiScanner;

    @Override
    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    protected void initMaps() {
        List<Class<? extends FilterBuilder>> availableBuilders = gephiScanner.getAvailableFilters();
        log.debug("Available filters size: " + availableBuilders.size());

        for (Class<? extends FilterBuilder> b : availableBuilders) {
            FilterWrapper function = new FilterWrapper(b);
            if (function.getFilter() instanceof Operator) {
                // Skip union, intersection, ...
                continue;
            }
            xmlMap.put(function.getId(), create(function));
            functionMap.put(function.getId(), function);
        }
    }

    @Override
    protected FilterXml create(FilterWrapper function) {
        return FilterCustomConverter.Converters.convert(function);
    }

    @Override
    protected FilterXml createNew() {
        return new FilterXml();
    }

    @Override
    public FiltersXml createNewContainer() {
        return new FiltersXml();
    }
}
