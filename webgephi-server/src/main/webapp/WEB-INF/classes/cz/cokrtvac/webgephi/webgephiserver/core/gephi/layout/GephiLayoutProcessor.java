package cz.cokrtvac.webgephi.webgephiserver.core.gephi.layout;

import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import cz.cokrtvac.webgephi.api.model.PropertyXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutProperty;
import org.slf4j.Logger;

import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 9.6.13
 * Time: 18:10
 */
public class GephiLayoutProcessor {

    @Inject
    private Logger log;

    @Inject
    private LayoutsPool layoutsPool;

    public GephiLayoutProcessor() {
    }

    /**
     * Apply layout function on graph
     *
     * @param workspaceWrapper - wokspace with graph
     * @param layoutXml        - definition of layout
     * @param repeat           - number of layout repetitions
     * @return
     */
    public WorkspaceWrapper process(WorkspaceWrapper workspaceWrapper, LayoutXml layoutXml, Integer repeat) {
        Layout layout = layoutsPool.getNewLayout(layoutXml.getId());
        layout.setGraphModel(workspaceWrapper.getGraphModel());

        // Set params from xml
        // Has to be here, setGraphModel resets settings - e.g. ForceAtlas2
        applySettings(layout, layoutXml);

        layout.initAlgo();
        for (int i = 0; i < repeat; i++) {
            layout.goAlgo();
        }
        layout.endAlgo();

        return workspaceWrapper;
    }

    private Layout applySettings(Layout layout, LayoutXml layoutXml) {
        for (LayoutProperty p : layout.getProperties()) {
            PropertyXml<?> pXml = layoutXml.getProperty(p.getCanonicalName());
            if (pXml != null) {
                try {
                    log.info(p.getProperty().getName() + " | " + p.getProperty().getValue() + " | " + pXml.getValue());
                    p.getProperty().setValue(pXml.getValue());
                } catch (Exception e) {
                    log.error("Property cannot be set.", e);
                }
            }
        }
        return layout;
    }
}
