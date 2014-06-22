package example;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingController;
import org.gephi.ranking.api.RankingModel;
import org.gephi.ranking.api.Transformer;
import org.gephi.ranking.plugin.transformer.AbstractSizeTransformer;
import org.gephi.statistics.plugin.GraphDistance;
import org.openide.util.Lookup;

import java.lang.reflect.InvocationTargetException;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 1. 5. 2014
 * Time: 12:24
 */
public class RankingExample {
    public void example() throws InvocationTargetException, IllegalAccessException {
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
        RankingController rankingController = Lookup.getDefault().lookup(RankingController.class);
        RankingModel rankingModel = rankingController.getModel();

// 31 - 41 ================================================================================
// Column with centrality attribute, which we wat to use for ranking
AttributeColumn centralityColumn = attributeModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
// Create ranking based on centrality attribute
Ranking centralityRanking = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, centralityColumn.getId());
// Create transformer - defines what we want to change (node size)
AbstractSizeTransformer sizeTransformer = (AbstractSizeTransformer) rankingModel.getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_SIZE);
// Set transformer parameters
sizeTransformer.setMinSize(3);
sizeTransformer.setMaxSize(20);
// Apply ranking, node size is updated
rankingController.transform(centralityRanking, sizeTransformer);
// ================================================================================
    }
}
