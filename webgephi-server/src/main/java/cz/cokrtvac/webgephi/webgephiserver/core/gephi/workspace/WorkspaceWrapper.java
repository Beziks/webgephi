package cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

/**
 * Wraps gephi workspace and related gephi tools.
 *
 * @author beziks
 */
public class WorkspaceWrapper {
    private Workspace workspace;
    private AttributeModel attributeModel;
    private GraphModel graphModel;
    private PreviewModel previewModel;

	/*
     * private ImportController importController;
	 * private FilterController filterController;
	 * private RankingController rankingController;
	 */

    /**
     * dont call directly, use {@link GephiWorkspaceProvider}
     *
     * @param workspace
     */
    WorkspaceWrapper(Workspace workspace) {
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace cannot be null");
        }

        this.workspace = workspace;

        attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel(workspace);
        graphModel = Lookup.getDefault().lookup(GraphController.class).getModel(workspace);
        previewModel = Lookup.getDefault().lookup(PreviewController.class).getModel(workspace);

		/*
         * importController = Lookup.getDefault().lookup(ImportController.class);
		 * filterController = Lookup.getDefault().lookup(FilterController.class);
		 * rankingController = Lookup.getDefault().lookup(RankingController.class);
		 */
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public AttributeModel getAttributeModel() {
        return attributeModel;
    }

    public GraphModel getGraphModel() {
        return graphModel;
    }

    public PreviewModel getPreviewModel() {
        return previewModel;
    }

    public AttributeColumn getNodeAttribute(String id) {
        return getAttributeModel().getNodeTable().getColumn(id);
    }

    public AttributeColumn getEdgeAttribute(String id) {
        return getAttributeModel().getEdgeTable().getColumn(id);
    }

    public AttributeColumn getAttribute(String id) {
        AttributeColumn attr = getNodeAttribute(id);
        if (attr == null) {
            attr = getEdgeAttribute(id);
        }
        return attr;
    }

    @Override
    public String toString() {
        return "WorkspaceWrapper [" + System.identityHashCode(getWorkspace()) + "]";
    }
}
