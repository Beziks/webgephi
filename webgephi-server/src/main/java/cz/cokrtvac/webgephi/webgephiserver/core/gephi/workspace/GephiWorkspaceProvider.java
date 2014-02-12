package cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace;

import cz.cokrtvac.webgephi.api.util.Log;
import cz.cokrtvac.webgephi.api.util.Stopwatch;
import org.gephi.project.api.Project;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.project.impl.WorkspaceProviderImpl;
import org.openide.util.Lookup;
import org.slf4j.Logger;

/**
 * NOT thread safe.
 * After use, call clear to recycle instance.
 * <p/>
 * Provides access to gephi tools, especially to workspace.
 * Because of gephi limitations, there has to be one project per thread and every project has to have one active workspace only.
 *
 * @author beziks
 */
public class GephiWorkspaceProvider {
    private static Logger log = Log.get(GephiWorkspaceProvider.class);

    private ProjectController projectController;

    // Wraps actual workspace and related gephi tools
    private WorkspaceWrapper workspaceWrapper;
    private WorkspaceProviderImpl workspaceProvider;

    private Project project;

    public GephiWorkspaceProvider() {
        Stopwatch stopwatch = new Stopwatch();

        projectController = Lookup.getDefault().lookup(ProjectController.class);

        synchronized (projectController) {
            log.info("Creating PROJECT !!!!!!!!!!!!!!!!!!!!!!!!!!!");
            projectController.newProject();
            project = projectController.getCurrentProject();
        }

        workspaceProvider = project.getLookup().lookup(WorkspaceProviderImpl.class);
        workspaceWrapper = createWorkspaceWrapper();

        stopwatch.print();
    }

    /**
     * @return actual workspace. Workspace is not threadsafe.
     */
    public WorkspaceWrapper getWorkspace() {
        return workspaceWrapper;
    }

    /**
     * Clear instance, new fresh workspace will be created
     */
    public void clear() {
        log.trace("Clearing workspace (" + workspaceWrapper + ")...");
        workspaceProvider.removeWorkspace(workspaceWrapper.getWorkspace());
        workspaceWrapper = createWorkspaceWrapper();
        log.trace("New workspace created  (" + workspaceWrapper + ")");
    }

    private WorkspaceWrapper createWorkspaceWrapper() {
        log.trace("Creating new workspace...");
        Workspace w = projectController.newWorkspace(project);
        WorkspaceWrapper ww = new WorkspaceWrapper(w);
        return ww;
    }

    @Override
    public String toString() {
        return "GephiWorkspaceProvider [" + workspaceWrapper + "]";
    }
}
