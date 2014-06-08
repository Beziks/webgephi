package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.statistics;

import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.FunctionProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import org.slf4j.Logger;

import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 9.6.13
 * Time: 18:10
 */
public class GephiStatisticsProcessor extends FunctionProcessor<StatisticsWrapper, StatisticXml> {

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
     * @param function
     * @return
     */
    @Override
    public StatisticsWrapper process(WorkspaceWrapper workspaceWrapper, StatisticsWrapper function, StatisticXml xml, Integer repeat) {
        function.getStatistics().execute(workspaceWrapper.getGraphModel(), workspaceWrapper.getAttributeModel());
        return function;
    }

    @Override
    protected StatisticsWrapper createNew(StatisticXml xml) {
        return statisticsPool.createNew(xml);
    }
}
