package example;

import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import java.lang.reflect.InvocationTargetException;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 1. 5. 2014
 * Time: 12:24
 */
public class ConcurrencyBugExample {
    public void example() throws InvocationTargetException, IllegalAccessException {
        ImportController importController = null;
        Workspace currentWorkspace = null;
        Workspace notCurrentWorkspace = null;
        ProjectController projectController = null;
        Container container = null;
// 31-37 ================================================================================
// Set current workspace to 'currentWorkspace'
        projectController.openWorkspace(currentWorkspace);
        DefaultProcessor defaultProcessor = new DefaultProcessor();
// Set processor to use different workspace than current
        defaultProcessor.setWorkspace(notCurrentWorkspace);
// Import graph to Workspace 'currentWorkspace'
        importController.process(container, new DefaultProcessor(), currentWorkspace);
// 39 - 52 ================================================================================ 		}
        @ServiceProvider(service = Processor.class, position = 10)
        class DefaultProcessor extends AbstractProcessor implements Processor {
            // ...
            // Method called during import
            public void process() {
                // ...
                // Get GraphModel from current workspace!!!
                GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
                // It should be:
                // Lookup.getDefault().lookup(GraphController.class).getModel(workspace)
                // ...
            }
// ...
        }
    }

    class AbstractProcessor {
    }

    ;

    public interface Processor {
    }

    ;
}
