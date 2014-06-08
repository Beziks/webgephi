package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.layout;

import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.FunctionProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import org.gephi.layout.spi.Layout;
import org.slf4j.Logger;

import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 9.6.13
 * Time: 18:10
 */
public class GephiLayoutProcessor extends FunctionProcessor<LayoutWrapper, LayoutXml> {

    @Inject
    private Logger log;

    @Inject
    private LayoutsPool layoutsPool;

    public GephiLayoutProcessor() {
    }

    @Override
    protected LayoutWrapper process(WorkspaceWrapper workspaceWrapper, LayoutWrapper function, LayoutXml xml, Integer repeat) {
        Layout layout = function.getLayout();
        layout.setGraphModel(workspaceWrapper.getGraphModel());

        // Set params from xml
        // Has to be here, setGraphModel resets settings - e.g. ForceAtlas2
        applySetting(function, xml, workspaceWrapper);

        layout.initAlgo();
        for (int i = 0; i < repeat; i++) {
            layout.goAlgo();
        }
        layout.endAlgo();

        return function;
    }

    @Override
    protected LayoutWrapper createNew(LayoutXml xml) {
        return layoutsPool.createNew(xml);
    }
}
