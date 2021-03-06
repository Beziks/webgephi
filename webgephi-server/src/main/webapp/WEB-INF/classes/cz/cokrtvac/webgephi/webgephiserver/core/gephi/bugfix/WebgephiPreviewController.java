package cz.cokrtvac.webgephi.webgephiserver.core.gephi.bugfix;

import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.*;
import org.gephi.preview.AbstractRenderTarget;
import org.gephi.preview.PreviewModelImpl;
import org.gephi.preview.api.*;
import org.gephi.preview.spi.*;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WebgephiPreviewController implements PreviewController {

    private PreviewModelImpl model;
    // Other controllers
    private final GraphController graphController;
    private final AttributeController attributeController;
    // Registered renderers
    private Renderer[] registeredRenderers = null;
    private Boolean anyPluginRendererRegistered = null;

    public WebgephiPreviewController(Workspace workspace) {
        graphController = Lookup.getDefault().lookup(GraphController.class);
        attributeController = Lookup.getDefault().lookup(AttributeController.class);

        model = workspace.getLookup().lookup(PreviewModelImpl.class);
        if (model == null) {
            model = new PreviewModelImpl(workspace, this);
            workspace.add(model);
        }
    }

    @Override
    public void refreshPreview() {
        refreshPreview(model.getWorkspace());
    }

    @Override
    public synchronized void refreshPreview(Workspace workspace) {
        GraphModel graphModel = graphController.getModel(workspace);
        AttributeModel attributeModel = attributeController.getModel(model.getWorkspace());
        PreviewModelImpl previewModel = getModel(workspace);
        previewModel.clear();

        // Directed graph?
        previewModel.getProperties().putValue(PreviewProperty.DIRECTED, graphModel.isDirected() || graphModel.isMixed());

        // Graph
        Graph graph = graphModel.getGraphVisible();
        if (previewModel.getProperties().getFloatValue(PreviewProperty.VISIBILITY_RATIO) < 1f) {
            float visibilityRatio = previewModel.getProperties().getFloatValue(PreviewProperty.VISIBILITY_RATIO);
            GraphView reducedView = graphModel.copyView(graph.getView());
            graph = graphModel.getGraph(reducedView);
            Node[] nodes = graph.getNodes().toArray();
            for (int i = 0; i < nodes.length; i++) {
                float r = (float) i / (float) nodes.length;
                if (r > visibilityRatio) {
                    graph.removeNode(nodes[i]);
                }
            }
        }

        Renderer[] renderers;
        if (!mousePressed) {
            renderers = model.getManagedEnabledRenderers();
        } else {
            ArrayList<Renderer> renderersList = new ArrayList<Renderer>();
            for (Renderer renderer : model.getManagedEnabledRenderers()) {
                // Only mouse responsive renderers will be called while mouse is pressed
                if (renderer instanceof MouseResponsiveRenderer) {
                    renderersList.add(renderer);
                }
            }

            renderers = renderersList.toArray(new Renderer[0]);
        }

        if (renderers == null) {
            renderers = getRegisteredRenderers();
        }

        // Build items
        for (ItemBuilder b : Lookup.getDefault().lookupAll(ItemBuilder.class)) {
            // Only build items of this builder if some renderer needs it:
            if (isItemBuilderNeeded(b, previewModel.getProperties(), renderers)) {
                try {
                    Item[] items = b.getItems(graph, attributeModel);
                    if (items != null) {
                        previewModel.loadItems(b.getType(), items);
                    }
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
            }
        }

        // Destrow view
        if (previewModel.getProperties().getFloatValue(PreviewProperty.VISIBILITY_RATIO) < 1f) {
            graphModel.destroyView(graph.getView());
        }

        // Refresh dimensions
        updateDimensions(previewModel, previewModel.getItems(Item.NODE));

        // Pre process renderers
        for (Renderer r : renderers) {
            r.preProcess(model);
        }
    }

    private boolean isItemBuilderNeeded(ItemBuilder itemBuilder, PreviewProperties properties, Renderer[] renderers) {
        for (Renderer r : renderers) {
            if (r.needsItemBuilder(itemBuilder, properties)) {
                return true;
            }
        }

        return false;
    }

    public void updateDimensions(PreviewModelImpl model, Item[] nodeItems) {
        float margin = model.getProperties().getFloatValue(PreviewProperty.MARGIN); // percentage
        float topLeftX = 0f;
        float topLeftY = 0f;
        float bottomRightX = 0f;
        float bottomRightY = 0f;

        for (Item nodeItem : nodeItems) {
            float x = (Float) nodeItem.getData("x");
            float y = (Float) nodeItem.getData("y");
            float s = ((Float) nodeItem.getData("size")) / 2f;

            if (x - s < topLeftX) {
                topLeftX = x - s;
            }
            if (y - s < topLeftY) {
                topLeftY = y - s;
            }
            if (x + s > bottomRightX) {
                bottomRightX = x + s;
            }
            if (y + s > bottomRightY) {
                bottomRightY = y + s;
            }
        }

        float marginWidth = (bottomRightX - topLeftX) * (margin / 100f);
        float marginHeight = (bottomRightY - topLeftY) * (margin / 100f);
        topLeftX -= marginWidth;
        topLeftY -= marginHeight;
        bottomRightX += marginWidth;
        bottomRightY += marginHeight;
        model.setDimensions(new Dimension((int) (bottomRightX - topLeftX), (int) (bottomRightY - topLeftY)));
        model.setTopLeftPosition(new Point((int) topLeftX, (int) topLeftY));
    }

    @Override
    public void render(RenderTarget target) {
        PreviewModelImpl m = getModel();
        render(target, m.getManagedEnabledRenderers(), m);
    }

    @Override
    public void render(RenderTarget target, Workspace workspace) {
        PreviewModelImpl m = getModel(workspace);
        render(target, m.getManagedEnabledRenderers(), m);
    }

    @Override
    public void render(RenderTarget target, Renderer[] renderers) {
        render(target, renderers, getModel());
    }

    @Override
    public void render(RenderTarget target, Renderer[] renderers, Workspace workspace) {
        render(target, renderers != null ? renderers : getModel(workspace).getManagedEnabledRenderers(), getModel(workspace));
    }

    private synchronized void render(RenderTarget target, Renderer[] renderers, PreviewModelImpl previewModel) {
        if (previewModel != null) {
            PreviewProperties properties = previewModel.getProperties();

            // Progress
            ProgressTicket progressTicket = null;
            if (target instanceof AbstractRenderTarget) {
                int tasks = 0;
                for (Renderer r : renderers) {
                    if (!mousePressed || r instanceof MouseResponsiveRenderer) {
                        for (String type : previewModel.getItemTypes()) {
                            for (Item item : previewModel.getItems(type)) {
                                if (r.isRendererForitem(item, properties)) {
                                    tasks++;
                                }
                            }
                        }
                    }
                }
                progressTicket = ((AbstractRenderTarget) target).getProgressTicket();
                Progress.switchToDeterminate(progressTicket, tasks);
            }

            // Render items
            for (Renderer r : renderers) {
                if (!mousePressed || r instanceof MouseResponsiveRenderer) {
                    for (String type : previewModel.getItemTypes()) {
                        for (Item item : previewModel.getItems(type)) {
                            if (r.isRendererForitem(item, properties)) {
                                r.render(item, target, properties);
                                Progress.progress(progressTicket);
                                if (target instanceof AbstractRenderTarget) {
                                    if (((AbstractRenderTarget) target).isCancelled()) {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public synchronized PreviewModelImpl getModel() {
        if (model == null) {
            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            if (pc.getCurrentWorkspace() != null) {
                return getModel(pc.getCurrentWorkspace());
            }
        }
        return model;
    }

    @Override
    public synchronized PreviewModelImpl getModel(Workspace workspace) {
        PreviewModelImpl m = workspace.getLookup().lookup(PreviewModelImpl.class);
        if (m == null) {
            m = new PreviewModelImpl(workspace);
            workspace.add(m);
        }
        return m;
    }

    @Override
    public RenderTarget getRenderTarget(String name) {
        return getRenderTarget(name, getModel());
    }

    @Override
    public RenderTarget getRenderTarget(String name, Workspace workspace) {
        return getRenderTarget(name, getModel(workspace));
    }

    private synchronized RenderTarget getRenderTarget(String name, PreviewModel m) {
        if (m != null) {
            for (RenderTargetBuilder rtb : Lookup.getDefault().lookupAll(RenderTargetBuilder.class)) {
                if (rtb.getName().equals(name)) {
                    return rtb.buildRenderTarget(m);
                }
            }
        }
        return null;
    }

    @Override
    public Renderer[] getRegisteredRenderers() {
        if (registeredRenderers == null) {
            LinkedHashMap<String, Renderer> renderers = new LinkedHashMap<String, Renderer>();
            for (Renderer r : Lookup.getDefault().lookupAll(Renderer.class)) {
                renderers.put(r.getClass().getName(), r);
            }

            for (Renderer r : renderers.values().toArray(new Renderer[0])) {
                Class superClass = r.getClass().getSuperclass();
                if (superClass != null && superClass.getName().startsWith("org.gephi.preview.plugin.renderers.")) {
                    // Replace default renderer with plugin by removing it
                    renderers.remove(superClass.getName());
                }
            }

            registeredRenderers = renderers.values().toArray(new Renderer[0]);
        }
        return registeredRenderers;
    }

    @Override
    public boolean isAnyPluginRendererRegistered() {
        if (anyPluginRendererRegistered == null) {
            anyPluginRendererRegistered = false;
            for (Renderer renderer : getRegisteredRenderers()) {
                if (!renderer.getClass().getName().startsWith("org.gephi.preview.plugin.renderers.")) {
                    anyPluginRendererRegistered = true;
                    break;
                }
            }
        }
        return anyPluginRendererRegistered;
    }

    private boolean mousePressed = false;

    @Override
    public boolean sendMouseEvent(PreviewMouseEvent event) {
        return sendMouseEvent(event, Lookup.getDefault().lookup(ProjectController.class).getCurrentWorkspace());
    }

    @Override
    public boolean sendMouseEvent(PreviewMouseEvent event, Workspace workspace) {
        if (workspace == null) {
            return false;
        }

        PreviewModel previewModel = getModel(workspace);

        // Avoid drag events arriving to listeners if they did not consume previous press event.
        if ((event.type != PreviewMouseEvent.Type.DRAGGED && event.type != PreviewMouseEvent.Type.RELEASED) || mousePressed) {
            for (PreviewMouseListener listener : previewModel.getEnabledMouseListeners()) {
                switch (event.type) {
                    case CLICKED:
                        listener.mouseClicked(event, previewModel.getProperties(), workspace);
                        break;
                    case PRESSED:
                        mousePressed = true;
                        listener.mousePressed(event, previewModel.getProperties(), workspace);
                        break;
                    case DRAGGED:
                        listener.mouseDragged(event, previewModel.getProperties(), workspace);
                        break;
                    case RELEASED:
                        mousePressed = false;
                        listener.mouseReleased(event, previewModel.getProperties(), workspace);
                }
                if (event.isConsumed()) {
                    return true;
                }
            }
        }

        mousePressed = false;// Avoid drag events arriving to listeners if they did not consume previous press event.
        return false;
    }
}
