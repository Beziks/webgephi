package example;

import org.gephi.graph.api.GraphModel;
import org.gephi.layout.plugin.force.yifanHu.YifanHu;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;

import java.lang.reflect.InvocationTargetException;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 1. 5. 2014
 * Time: 12:24
 */
public class LayoutExample {
    public void example() throws InvocationTargetException, IllegalAccessException {
        GraphModel graphModel = null;


// 20 - 29 ================================================================================
YifanHu yifanHu = new YifanHu(); // Factory
YifanHuLayout layout = yifanHu.buildLayout(); // Layout function
layout.setGraphModel(graphModel); // Set the graph to work with
layout.initAlgo();
layout.resetPropertiesValues(); // Set default values
layout.getProperties()[0].getProperty().setValue(200f); // Change first param (OptimalDistance)
for (int i = 0; i < 100 && layout.canAlgo(); i++) {
    layout.goAlgo(); // Apply layout function
}
layout.endAlgo();
// ================================================================================
    }
}
