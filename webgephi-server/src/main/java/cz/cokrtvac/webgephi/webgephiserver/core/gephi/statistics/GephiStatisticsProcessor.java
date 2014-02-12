package cz.cokrtvac.webgephi.webgephiserver.core.gephi.statistics;

import cz.cokrtvac.webgephi.webgephiserver.core.gephi.statistics.StatisticsWrapper.StatisticsProperty;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import cz.cokrtvac.webgephi.api.model.PropertyXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;
import org.slf4j.Logger;

import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 9.6.13
 * Time: 18:10
 */
public class GephiStatisticsProcessor {

    @Inject
    private Logger log;

    @Inject
    private StatisticsPool statisticsPool;

    public GephiStatisticsProcessor() {
    }

    /**
     * Apply statistics function on graph
     *
     * @param workspaceWrapper - wokspace with graph
     * @param statisticXml     - definition of statistic
     * @return
     */
    public StatisticsWrapper process(WorkspaceWrapper workspaceWrapper, StatisticXml statisticXml) {
        StatisticsWrapper statistic = statisticsPool.getNewStatistics(statisticXml.getId());
        applySettings(statistic, statisticXml);

        statistic.execute(workspaceWrapper.getGraphModel(), workspaceWrapper.getAttributeModel());

        return statistic;
    }

    /**
     * Set parameters values according to xml
     *
     * @param sw
     * @param statisticXml
     * @return
     */
    private StatisticsWrapper applySettings(StatisticsWrapper sw, StatisticXml statisticXml) {
        for (StatisticsProperty p : sw.getProperties()) {
            PropertyXml<?> pXml = statisticXml.getProperty(p.getName());
            if (pXml != null) {
                try {
                    log.info(p.getName() + " | " + p.getValue() + " | " + pXml.getValue());
                    p.setValue(pXml.getValue());
                } catch (Exception e) {
                    log.error("Property cannot be set.", e);
                }
            }
        }
        return sw;
    }
}
