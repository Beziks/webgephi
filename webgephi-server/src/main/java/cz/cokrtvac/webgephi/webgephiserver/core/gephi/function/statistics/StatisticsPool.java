package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.statistics;

import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticsXml;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.GephiScanner;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.FunctionsPool;
import org.gephi.statistics.spi.DynamicStatistics;
import org.gephi.statistics.spi.StatisticsBuilder;
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
public class StatisticsPool extends FunctionsPool<StatisticsWrapper, StatisticXml, StatisticsXml> {

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
        List<StatisticsBuilder> availableStatistics = gephiScanner.getAvailableStatistics();
        log.debug("Available statistics size: " + String.valueOf(availableStatistics.size()));

        for (StatisticsBuilder b : availableStatistics) {
            if (b.getStatistics() instanceof DynamicStatistics) {
                // skip dynamic statistics, like in desktop gui
                continue;
            }

            StatisticsWrapper function = new StatisticsWrapper(b);
            xmlMap.put(function.getId(), create(function));
            functionMap.put(function.getId(), function);
        }
    }

    @Override
    protected StatisticXml createNew() {
        return new StatisticXml();
    }

    @Override
    public StatisticsXml createNewContainer() {
        return new StatisticsXml();
    }
}
