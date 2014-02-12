package cz.cokrtvac.webgephi.webgephiserver.core.gephi.statistics;

import cz.cokrtvac.webgephi.webgephiserver.core.gephi.GephiScanner;
import cz.cokrtvac.webgephi.webgephiserver.core.WebgephiXmlFactory;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticsXml;
import org.gephi.statistics.spi.DynamicStatistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.*;

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
public class StatisticsPool {

    @Inject
    private Logger log;

    @Inject
    private GephiScanner gephiScanner;

    private StatisticsXml statistics;
    private Map<String, StatisticXml> statisticXmlsMap = new HashMap<String, StatisticXml>();
    private Map<String, StatisticsBuilder> statisticsBuilderMap = new HashMap<String, StatisticsBuilder>();

    @PostConstruct
    public void init() {
        log.info("Initializing app setting.");
        initMaps();
        initStatistics();
        log.info("Initializing done.");
    }

    public StatisticsXml getAvailableStatistics() {
        return statistics;
    }

    public StatisticsWrapper getNewStatistics(String id) {
        StatisticsBuilder sb = statisticsBuilderMap.get(id);
        return new StatisticsWrapper(sb);
    }

    private void initMaps() {
        List<StatisticsBuilder> availableStatistics = gephiScanner.getAvailableStatistics();
        log.debug("Available statistics size: " + String.valueOf(availableStatistics.size()));

        for (StatisticsBuilder b : availableStatistics) {
            if (b.getStatistics() instanceof DynamicStatistics) {
                // skip dynamic statistics, like in desktop gui
                continue;
            }

            StatisticXml statisticXml = WebgephiXmlFactory.create(new StatisticsWrapper(b));
            statisticXmlsMap.put(statisticXml.getId(), statisticXml);
            statisticsBuilderMap.put(statisticXml.getId(), b);
        }
    }

    private void initStatistics() {
        List<StatisticXml> all = new ArrayList<StatisticXml>(statisticXmlsMap.values());
        Collections.sort(all, new Comparator<StatisticXml>() {
            @Override
            public int compare(StatisticXml o1, StatisticXml o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        statistics = new StatisticsXml();
        statistics.getStatistics().addAll(all);
    }

}
