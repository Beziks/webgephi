package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.ranking;

import cz.cokrtvac.webgephi.api.model.ranking.RankingXml;
import cz.cokrtvac.webgephi.api.model.ranking.RankingsXml;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.FunctionsPool;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.Transformer;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Provides info about all available ranking functions in application.
 * Runs after deploy.
 * <p/>
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 15:18
 */
@Singleton
@Startup
public class RankingsPool extends FunctionsPool<RankingWrapper, RankingXml, RankingsXml> {
    @Inject
    private Logger log;

    @Override
    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    protected void initMaps() {
        // Manually add possible configurations
        // NODE
        RankingWrapper nodeColor = new RankingWrapper(
                "nodeColor",
                "Node color ranking",
                "Ranking according to node attribute, changes node color.",
                Transformer.RENDERABLE_COLOR,
                Ranking.NODE_ELEMENT);
        functionMap.put(nodeColor.getId(), nodeColor);

        RankingWrapper nodeSize = new RankingWrapper(
                "nodeSize",
                "Node size ranking",
                "Ranking according to node attribute, changes node size.",
                Transformer.RENDERABLE_SIZE,
                Ranking.NODE_ELEMENT);
        functionMap.put(nodeSize.getId(), nodeSize);

       /* RankingWrapper nodeLabelColor = new RankingWrapper(
                "nodeLabelColor",
                "Node label color ranking",
                "Ranking according to node attribute, changes color of node label.",
                Transformer.LABEL_COLOR,
                Ranking.NODE_ELEMENT);
        functionMap.put(nodeLabelColor.getAttributeId(), nodeLabelColor);  */

       /* RankingWrapper nodeLabelSize = new RankingWrapper(
                "nodeLabelSize",
                "Node label size ranking",
                "Ranking according to node attribute, changes size of node label.",
                Transformer.LABEL_SIZE,
                Ranking.NODE_ELEMENT);
        functionMap.put(nodeLabelSize.getAttributeId(), nodeLabelSize);     */

        // EDGE
        RankingWrapper edgeColor = new RankingWrapper(
                "edgeColor",
                "Edge color ranking",
                "Ranking according to edge attribute, changes edge color.",
                Transformer.RENDERABLE_COLOR,
                Ranking.EDGE_ELEMENT);
        functionMap.put(edgeColor.getId(), edgeColor);

       /* RankingWrapper edgeSize = new RankingWrapper(
                "edgeSize",
                "Edge size ranking",
                "Ranking according to edge attribute, changes edge size.",
                Transformer.RENDERABLE_SIZE,
                Ranking.EDGE_ELEMENT);
        functionMap.put(edgeSize.getAttributeId(), edgeSize);   */

       /* RankingWrapper edgeLabelColor = new RankingWrapper(
                "edgeLabelColor",
                "Edge label color ranking",
                "Ranking according to edge attribute, changes color of edge label.",
                Transformer.LABEL_COLOR,
                Ranking.EDGE_ELEMENT);
        functionMap.put(edgeLabelColor.getAttributeId(), edgeLabelColor);  */

       /* RankingWrapper edgeLabelSize = new RankingWrapper(
                "edgeLabelSize",
                "Edge label size ranking",
                "Ranking according to edge attribute, changes size of edge label.",
                Transformer.LABEL_SIZE,
                Ranking.EDGE_ELEMENT);
        functionMap.put(edgeLabelSize.getAttributeId(), edgeLabelSize); */

        for (RankingWrapper f : functionMap.values()) {
            xmlMap.put(f.getId(), create(f));
        }
    }

    @Override
    protected RankingXml createNew() {
        return new RankingXml();
    }

    @Override
    public RankingsXml createNewContainer() {
        return new RankingsXml();
    }
}
