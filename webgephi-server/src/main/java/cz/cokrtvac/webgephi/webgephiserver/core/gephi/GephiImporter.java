package cz.cokrtvac.webgephi.webgephiserver.core.gephi;

import cz.cokrtvac.webgephi.webgephiserver.core.gephi.bugfix.WebgephiImportProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.spi.FileImporter;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.StringReader;

public class GephiImporter {
    public static final String GEXF_FORMAT = ".gexf";

    @Inject
    private Logger log;

    @Inject
    private ImportController importController;

    public WorkspaceWrapper importGexf(String gexf, WorkspaceWrapper workspaceWrapper) {
        log.trace("Importing from workspace: " + workspaceWrapper.getWorkspace().toString());

        FileImporter fileImporter = importController.getFileImporter(GEXF_FORMAT);
        Container container = importController.importFile(new StringReader(gexf), fileImporter);
        container.setAutoScale(false);
        // TODO container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED); // Force UNDIRECTED

        // Append imported data to GraphAPI
        importController.process(container, new WebgephiImportProcessor(), workspaceWrapper.getWorkspace());

        log.trace("Import done");
        return workspaceWrapper;
    }

}
