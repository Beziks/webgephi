package cz.cokrtvac.webgephi.webgephiserver.core.gephi;

import cz.cokrtvac.webgephi.webgephiserver.core.gephi.bugfix.WebgephiSvgExporter;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.EdgeColor;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.StringWriter;

public class GephiExporter {
    public static final String GEXF_FORMAT = ".gexf";

    @Inject
    private Logger log;

    @Inject
    private ExportController exportController;

    @Inject
    private GephiImporter gephiImporter;

    public String toSvg(String gexf, WorkspaceWrapper workspaceWrapper) {
        gephiImporter.importGexf(gexf, workspaceWrapper);
        return toSvg(workspaceWrapper);
    }

    public String toSvg(WorkspaceWrapper workspaceWrapper) {
        WebgephiSvgExporter svgExporter = new WebgephiSvgExporter();

        PreviewModel model = workspaceWrapper.getPreviewModel();
        PreviewProperties prop = model.getProperties();
        prop.putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        prop.putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, Boolean.FALSE);
        prop.putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(EdgeColor.Mode.ORIGINAL));
        prop.putValue(PreviewProperty.NODE_LABEL_FONT, prop.getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(8));

        svgExporter.setWorkspace(workspaceWrapper.getWorkspace());

        log.debug("NODES:::: " + workspaceWrapper.getGraphModel().getGraph().getNodeCount());

        StringWriter sw = new StringWriter();
        exportController.exportWriter(sw, svgExporter);
        return sw.toString();
    }

    public String toGexf(WorkspaceWrapper workspaceWrapper) {
        GraphExporter exporter = (GraphExporter) exportController.getExporter(GEXF_FORMAT);
        exporter.setWorkspace(workspaceWrapper.getWorkspace());
        exporter.setExportVisible(true);
        StringWriter sw = new StringWriter();
        exportController.exportWriter(sw, (CharacterExporter) exporter);
        return sw.toString();
    }

}
