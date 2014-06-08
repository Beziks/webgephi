package cz.cokrtvac.webgephi.webgephiserver.core.gephi;

import cz.cokrtvac.webgephi.webgephiserver.core.gephi.bugfix.WebgephiImportProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.spi.FileImporter;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GephiImporter {
    public static final String GEXF_FORMAT = ".gexf";
    public static final String CSV_FORMAT = ".csv";

    private Logger log;
    private ImportController importController;

    private static final List<FormatResolver> FORMAT_RESOLVERS = init();

    private static List<FormatResolver> init() {
        List<FormatResolver> r = new ArrayList<FormatResolver>();
        r.add(new AbstractFormatResolver(GEXF_FORMAT, "GEXF") {
            @Override
            public boolean isFormat(String content) {
                return content.contains("<gexf");
            }
        });

        r.add(new AbstractFormatResolver(".dl", "dl", "UCINET") {
            // UCINET DL format is the most common file format used by UCINET package.
            @Override
            public boolean isFormat(String content) {
                return content.startsWith("dl") || content.startsWith("DL");
            }
        });

        r.add(new AbstractFormatResolver(".gdf", "GDF", "GUESS") {
            // GDF is the file format used by GUESS. It is built like a database table or a coma separated file (CSV)
            @Override
            public boolean isFormat(String content) {
                return content.startsWith("nodedef>");
            }
        });

        r.add(new AbstractFormatResolver(".gml", "GML") {
            // GML (Graph Modeling Language) is a text file format supporting network data with a very easy syntax. It is used by Graphlet, Pajek, yEd, LEDA and NetworkX.
            @Override
            public boolean isFormat(String content) {
                return content.startsWith("graph") && content.contains("[") && content.contains("]");
            }
        });

        r.add(new AbstractFormatResolver(".graphml", "GraphML") {
            // The GraphML file format uses .graphml extension and is XML structured. This format is supported by NodeXL, Sonivis, GUESS and NetworkX.
            @Override
            public boolean isFormat(String content) {
                return content.contains("<graphml");
            }
        });

        r.add(new AbstractFormatResolver(".net", "NET", "Pajek") {
            // This format use NET extension and is easy to use. It is supported by nearly most of graph softwares, including Pajek, NodeXL and NetworkX.
            @Override
            public boolean isFormat(String content) {
                return content.contains("*Vertices") && content.contains("*Edges");
            }
        });

        r.add(new AbstractFormatResolver(".vna", "VNA") {

            @Override
            public boolean isFormat(String content) {
                return content.contains("*Node") && content.contains("*Tie");
            }
        });

        r.add(new AbstractFormatResolver(".dot", ".gv", "GV", "DOT", "GraphViz") {
            // DOT is the text file format of the suite GraphViz. NetworkX, Tulip or ZGRViewer can import DOT files as well.
            @Override
            public boolean isFormat(String content) {
                return content.startsWith("digraph");
            }
        });

        r.add(new AbstractFormatResolver(".tlp", "TLP", "Tulip") {
            // TLP is the file format used by Tulip. Only network topology (nodes and edges) is currently supported.
            @Override
            public boolean isFormat(String content) {
                return content.startsWith("(tlp");
            }
        });

        r.add(new AbstractFormatResolver(CSV_FORMAT, "CSV", ".edges", "EDGES", "MATRIX") {
            @Override
            public boolean isFormat(String content) {
                return content.startsWith(";");
            }
        });
        return r;
    }

    @Inject
    public GephiImporter(Logger log, ImportController importController) {
        this.log = log;
        this.importController = importController;
    }

    public WorkspaceWrapper importGexf(String gexf, WorkspaceWrapper workspaceWrapper) {
        return importGraph(gexf, GEXF_FORMAT, workspaceWrapper);
    }

    public WorkspaceWrapper importGraph(String content, String formatName, WorkspaceWrapper workspaceWrapper) {
        log.trace("Importing to workspace: " + workspaceWrapper.getWorkspace().toString());
        String extension;
        if (GEXF_FORMAT.equalsIgnoreCase(formatName)) {
            // Speed optimization
            extension = GEXF_FORMAT;
        } else {
            extension = resolveFormat(content, formatName);
            if (extension == null) {
                log.warn("Unknown extension for format " + formatName + " using it as extension");
                extension = formatName;
            }
        }

        log.info("Importing graph in format " + formatName + "->" + extension);

        FileImporter fileImporter = importController.getFileImporter(extension);
        if (fileImporter == null) {
            log.warn("No importer found for " + extension + ", defaulting to CSV importer");
            fileImporter = importController.getFileImporter(CSV_FORMAT);
        }

        InputStream is = null;
        try {
            is = new ByteArrayInputStream(content.getBytes());
            Container container = importController.importFile(is, fileImporter);
            container.setAutoScale(false);
            //container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED); // Force UNDIRECTED

            // Append imported data to GraphAPI
            importController.process(container, new WebgephiImportProcessor(), workspaceWrapper.getWorkspace());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
        log.trace("Import done");
        return workspaceWrapper;
    }

    public String resolveFormat(String content, String formatName) {
        // Name first
        if (formatName != null) {
            for (FormatResolver r : FORMAT_RESOLVERS) {
                if (r.getNames().contains(formatName)) {
                    return r.getExtension();
                }
            }
        }

        // Otherwise check content
        for (FormatResolver r : FORMAT_RESOLVERS) {
            if (r.isFormat(content)) {
                return r.getExtension();
            }
        }

        return null;
    }

    interface FormatResolver {
        public String getExtension();

        public Set<String> getNames();

        public boolean isFormat(String content);
    }

    static abstract class AbstractFormatResolver implements FormatResolver {
        private String extension;
        private Set<String> names;

        AbstractFormatResolver(String extension, String... names) {
            Set<String> set = new HashSet<String>();
            for (String n : names) {
                set.add(n.toLowerCase());
            }
            set.add(extension.toLowerCase());
            set.add(extension.substring(1).toLowerCase());
            this.extension = extension;
            this.names = set;
        }

        public final String getExtension() {
            return extension;
        }

        public final Set<String> getNames() {
            return names;
        }

        public abstract boolean isFormat(String content);
    }
}
