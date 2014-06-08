package cz.cokrtvac.webgephi.webgephiserver.core.gephi;

import cz.cokrtvac.webgephi.api.util.IOUtil;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.GephiWorkspaceProvider;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import org.gephi.graph.api.Node;
import org.gephi.io.importer.impl.ImportControllerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class GephiImporterTest {
    private Logger log = LoggerFactory.getLogger(getClass());
    private String[] graphs;
    private GephiImporter gephiImporter;
    private GephiWorkspaceProvider workspaceProvider;

    @BeforeClass
    public void init() {
        gephiImporter = new GephiImporter(log, new ImportControllerImpl());
        workspaceProvider = new GephiWorkspaceProvider();
        graphs = new String[]{"misserables.csv", "misserables.dl", "misserables.gdf", "misserables.gexf", "misserables.gml", "misserables.graphml", "misserables.net", "misserables.vna"};
    }

    @Test
    public void testAll() throws Exception {
        for (String s : graphs) {
            String content = IOUtil.readAsString(getClass().getClassLoader().getResourceAsStream("graphs/" + s));
            String format = "nonsence";

            testImportGraph(content, format, s);
        }

        for (String s : graphs) {
            String content = IOUtil.readAsString(getClass().getClassLoader().getResourceAsStream("graphs/" + s));
            String format = s.substring("misserables".length());

            testImportGraph(content, format, s);
        }
    }

    public void testImportGraph(String content, String format, String filename) throws Exception {
        WorkspaceWrapper ww = workspaceProvider.getWorkspace();
        try {
            gephiImporter.importGraph(content, format, ww);

            int cnt = 0;
            for (Node n : ww.getGraphModel().getGraph().getNodes()) {
                log.info(filename + ": Node: " + n + " | " + n.getId());
                cnt++;
            }
            Assert.assertEquals(cnt, 77);
            log.info(filename + ": Number of nodes is OK");
        } finally {
            workspaceProvider.clear();
        }
    }
}