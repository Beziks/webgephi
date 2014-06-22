package example;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.statistics.plugin.GraphDistance;
import org.gephi.statistics.plugin.builder.GraphDistanceBuilder;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.Lookup;

import java.lang.reflect.InvocationTargetException;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 1. 5. 2014
 * Time: 12:24
 */
public class StatisticsExample {
    public void example() throws InvocationTargetException, IllegalAccessException {
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();


// 29 - 41 ================================================================================
// Create statistics builder
        StatisticsBuilder builder = new GraphDistanceBuilder();
// Get statistics function implementation
        GraphDistance gd = (GraphDistance) builder.getStatistics();
        gd.setDirected(true); // Set parameter directly using setter
        gd.execute(graphModel, attributeModel); // apply function
        String report = gd.getReport(); // Get html report

// We can iterate over metric values of nodes too
        AttributeColumn col = attributeModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
        for (Node n : graphModel.getGraph().getNodes()) {
            Double centrality = (Double) n.getNodeData().getAttributes().getValue(col.getIndex());
        }
// ================================================================================
    }
}
