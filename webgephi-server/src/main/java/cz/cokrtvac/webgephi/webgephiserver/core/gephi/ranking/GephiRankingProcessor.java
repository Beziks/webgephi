package cz.cokrtvac.webgephi.webgephiserver.core.gephi.ranking;

import cz.cokrtvac.webgephi.api.model.PropertyXml;
import cz.cokrtvac.webgephi.api.model.ranking.RankingXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.statistics.StatisticsPool;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.statistics.StatisticsWrapper;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.statistics.StatisticsWrapper.StatisticsProperty;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.ranking.RankingModelImpl;
import org.gephi.ranking.api.Interpolator;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingModel;
import org.gephi.ranking.api.Transformer;
import org.gephi.ranking.plugin.transformer.AbstractColorTransformer;
import org.gephi.ranking.plugin.transformer.AbstractSizeTransformer;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.awt.*;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 9.6.13
 * Time: 18:10
 */
public class GephiRankingProcessor {

    @Inject
    private Logger log;

    @Inject
    private StatisticsPool statisticsPool;

    public GephiRankingProcessor() {
    }

    /**
     * Apply statistics function on graph
     *
     * @param workspaceWrapper - wokspace with graph
     * @param rankingXml       - definition of ranking
     * @return
     */
    public WorkspaceWrapper process(WorkspaceWrapper workspaceWrapper, RankingXml rankingXml) {
        //RankingController rankingController = Lookup.getDefault().lookup(RankingController.class);

        String attributeName = (String) rankingXml.getProperty(RankingXml.RANKING_ATTRIBUTE_ID).getValue();
        AttributeColumn attr = workspaceWrapper.getAttributeModel().getNodeTable().getColumn(attributeName);
        if (attr == null) {
            throw new IllegalArgumentException("No such attribute in graph nodes: " + attributeName);
        }

        //RankingModel rankingModel = rankingController.getModel(workspaceWrapper.getWorkspace());
        RankingModel rankingModel = new RankingModelImpl(workspaceWrapper.getWorkspace());
        workspaceWrapper.getWorkspace().add(rankingModel);
        Ranking ranking = rankingModel.getRanking(Ranking.NODE_ELEMENT, attr.getId());

        Transformer transformer = null;
        if (RankingXml.COLOR_RANKING_ID.equals(rankingXml.getId())) {
            AbstractColorTransformer colorTransformer = (AbstractColorTransformer) rankingModel.getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_COLOR);
            transformer = colorTransformer;
            try {
                String c1 = (String) rankingXml.getProperty(RankingXml.COLOR_RANKING_PROPERTY_COLOR1).getValue();
                String c2 = (String) rankingXml.getProperty(RankingXml.COLOR_RANKING_PROPERTY_COLOR2).getValue();
                Integer ci1 = Integer.parseInt(c1, 16);
                Integer ci2 = Integer.parseInt(c2, 16);
                colorTransformer.setColors(new Color[]{new Color(ci1), new Color(ci2)});
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid color input: " + e.getMessage(), e);
            }
        } else if (RankingXml.SIZE_RANKING_ID.equals(rankingXml.getId())) {
            AbstractSizeTransformer sizeTransformer = (AbstractSizeTransformer) rankingModel.getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_SIZE);
            transformer = sizeTransformer;
            Float f1 = (Float) rankingXml.getProperty(RankingXml.SIZE_RANKING_PROPERTY_SIZE1).getValue();
            Float f2 = (Float) rankingXml.getProperty(RankingXml.SIZE_RANKING_PROPERTY_SIZE2).getValue();

            sizeTransformer.setMinSize(f1);
            sizeTransformer.setMaxSize(f2);
        } else {
            throw new IllegalArgumentException("No such ranking function: " + rankingXml.getId());
        }

        // Does not work, bug again...
        // rankingController.transform(ranking, transformer);
        transform(workspaceWrapper, rankingModel, ranking, transformer);
        return workspaceWrapper;
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
