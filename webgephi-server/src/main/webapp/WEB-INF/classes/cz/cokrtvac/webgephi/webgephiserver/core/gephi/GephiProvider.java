package cz.cokrtvac.webgephi.webgephiserver.core.gephi;

import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.ImportController;
import org.openide.util.Lookup;

import javax.enterprise.inject.Produces;


/**
 * Provides Gephi Singletons
 *
 * @author beziks
 */
public class GephiProvider {
    private ImportController importController;
    private ExportController exportController;

    public GephiProvider() {
        importController = Lookup.getDefault().lookup(ImportController.class);
        exportController = Lookup.getDefault().lookup(ExportController.class);
    }

    @Produces
    public ImportController getImportController() {
        return importController;
    }

    @Produces
    public ExportController getExportController() {
        return exportController;
    }
}
