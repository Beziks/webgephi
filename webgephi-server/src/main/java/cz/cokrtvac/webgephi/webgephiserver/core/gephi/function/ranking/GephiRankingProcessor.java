package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.ranking;

import cz.cokrtvac.webgephi.api.model.ranking.RankingXml;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.FunctionProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.ranking.RankingModelImpl;
import org.gephi.ranking.api.Interpolator;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingModel;
import org.gephi.ranking.api.Transformer;
import org.slf4j.Logger;

import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 9.6.13
 * Time: 18:10
 */
public class GephiRankingProcessor extends FunctionProcessor<RankingWrapper, RankingXml> {

    @Inject
    private Logger log;

    @Inject
    private RankingsPool rankingsPool;

    public GephiRankingProcessor() {
    }

    @Override
    public RankingWrapper process(WorkspaceWrapper workspaceWrapper, RankingWrapper function, RankingXml rankingXml, Integer repeat) {
        RankingModel rankingModel = new RankingModelImpl(workspaceWrapper.getWorkspace());
        RankingWrapper.RankingSetting sett = function.getSetting(workspaceWrapper, rankingModel);
        // Does not work, bug again...
        // rankingController.transform(ranking, transformer);
        transform(workspaceWrapper, rankingModel, sett.getRanking(), sett.getTransformer());
        return function;
    }

    @Override
    protected RankingWrapper createNew(RankingXml xml) {
        return rankingsPool.createNew(xml);
    }

    /**
     * Copied from gephi toolkit - bugfix
     *
     * @param model
     * @param ranking
     * @param transformer
     * @see org.gephi.ranking.RankingControllerImpl
     */
    public static void transform(WorkspaceWrapper workspaceWrapper, RankingModel model, Ranking ranking, Transformer transformer) {
        GraphModel graphModel = workspaceWrapper.getGraphModel();
        HierarchicalGraph graph = graphModel.getHierarchicalGraphVisible();
        Interpolator interpolator = model.getInterpolator();

        if (ranking.getElementType().equals(Ranking.NODE_ELEMENT)) {
            for (Node node : graph.getNodes().toArray()) {
                Number value = ranking.getValue(node);
                if (value != null) {
                    float normalizedValue = ranking.normalize(value);
                    if (transformer.isInBounds(normalizedValue)) {
                        normalizedValue = interpolator.interpolate(normalizedValue);
                        transformer.transform(node.getNodeData(), normalizedValue);
                    }
                }
            }
        } else if (ranking.getElementType().equals(Ranking.EDGE_ELEMENT)) {
            for (Edge edge : graph.getEdgesAndMetaEdges().toArray()) {
                Number value = ranking.getValue(edge);
                if (value != null) {
                    float normalizedValue = ranking.normalize(value);
                    if (transformer.isInBounds(normalizedValue)) {
                        normalizedValue = interpolator.interpolate(normalizedValue);
                        transformer.transform(edge.getEdgeData(), normalizedValue);
                    }
                }
            }
        }
    }
}
