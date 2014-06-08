package cz.cokrtvac.webgephi.webgephiserver.core.filter;

import cz.cokrtvac.webgephi.api.util.IOUtil;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.GephiImporter;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.GephiScanner;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.GephiWorkspaceProvider;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeUtils;
import org.gephi.filters.api.Range;
import org.gephi.filters.plugin.attribute.AttributeEqualBuilder;
import org.gephi.filters.plugin.attribute.AttributeNonNullBuilder;
import org.gephi.filters.plugin.attribute.AttributeRangeBuilder;
import org.gephi.filters.plugin.dynamic.DynamicRangeBuilder;
import org.gephi.filters.plugin.edge.EdgeWeightBuilder;
import org.gephi.filters.plugin.edge.SelfLoopFilterBuilder;
import org.gephi.filters.plugin.graph.*;
import org.gephi.filters.plugin.hierarchy.FlattenBuilder;
import org.gephi.filters.plugin.operator.INTERSECTIONBuilder;
import org.gephi.filters.plugin.operator.MASKBuilderEdge;
import org.gephi.filters.plugin.operator.NOTBuilderNode;
import org.gephi.filters.plugin.operator.UNIONBuilder;
import org.gephi.filters.plugin.partition.InterEdgesBuilder;
import org.gephi.filters.plugin.partition.IntraEdgesBuilder;
import org.gephi.filters.plugin.partition.PartitionBuilder;
import org.gephi.filters.plugin.partition.PartitionCountBuilder;
import org.gephi.filters.spi.CategoryBuilder;
import org.gephi.filters.spi.Filter;
import org.gephi.filters.spi.FilterBuilder;
import org.gephi.filters.spi.FilterProperty;
import org.gephi.io.importer.impl.ImportControllerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 8. 5. 2014
 * Time: 21:01
 */
public class FilterScan {
    private Logger log = LoggerFactory.getLogger(getClass());
    GephiScanner scanner;


    @BeforeClass
    public void init() {
        scanner = new GephiScanner(log, new GephiImporter(log, new ImportControllerImpl()));
    }

    @Test
    public void testIndependentFilters() {
        List<FilterBuilder> builders = scanner.getAvailableFilterBuilders();
        List<Class<?>> builderClassses = new ArrayList<Class<?>>();

        HashSet<String> categories = new HashSet<String>();

        for (FilterBuilder b : builders) {
            categories.add(b.getCategory().getName() + " - " + (b.getCategory().getParent() != null ? b.getCategory().getParent().getName() : ""));
            builderClassses.add(b.getClass());
        }
        // LevelBuilder has no @ServiceProvider annotation
        Assert.assertTrue(builderClassses.contains(DegreeRangeBuilder.class));
        Assert.assertTrue(builderClassses.contains(KCoreBuilder.class));
        Assert.assertTrue(builderClassses.contains(FlattenBuilder.class));
        Assert.assertTrue(builderClassses.contains(MutualDegreeRangeBuilder.class));
        Assert.assertTrue(builderClassses.contains(UNIONBuilder.class));
        Assert.assertTrue(builderClassses.contains(NeighborsBuilder.class));
        Assert.assertTrue(builderClassses.contains(GiantComponentBuilder.class));
        Assert.assertTrue(builderClassses.contains(MASKBuilderEdge.class));
        Assert.assertTrue(builderClassses.contains(SelfLoopFilterBuilder.class));
        Assert.assertTrue(builderClassses.contains(InDegreeRangeBuilder.class));
        Assert.assertTrue(builderClassses.contains(NOTBuilderNode.class));
        Assert.assertTrue(builderClassses.contains(OutDegreeRangeBuilder.class));
        Assert.assertTrue(builderClassses.contains(EdgeWeightBuilder.class));
        Assert.assertTrue(builderClassses.contains(EgoBuilder.class));
        Assert.assertTrue(builderClassses.contains(INTERSECTIONBuilder.class));

        log.info("Categories:\n=====================================");
        for (String s : categories) {
            log.info(s);
        }

        log.info("DONE " + builders.size());
    }

    @Test
    public void testFilterCategories() throws Exception {
        List<CategoryBuilder> builders = scanner.getAvailableFilterCategories();
        HashSet<String> categories = new HashSet<String>();
        List<Class<?>> builderClassses = new ArrayList<Class<?>>();

        for (CategoryBuilder b : builders) {
            builderClassses.add(b.getClass());
            categories.add(b.getCategory().getName() + " - " + (b.getCategory().getParent() != null ? b.getCategory().getParent().getName() : ""));
        }

        Assert.assertTrue(builderClassses.contains(AttributeEqualBuilder.class));
        Assert.assertTrue(builderClassses.contains(AttributeNonNullBuilder.class));
        Assert.assertTrue(builderClassses.contains(AttributeRangeBuilder.class));
        Assert.assertTrue(builderClassses.contains(DynamicRangeBuilder.class));
        Assert.assertTrue(builderClassses.contains(InterEdgesBuilder.class));
        Assert.assertTrue(builderClassses.contains(IntraEdgesBuilder.class));
        Assert.assertTrue(builderClassses.contains(PartitionBuilder.class));
        Assert.assertTrue(builderClassses.contains(PartitionCountBuilder.class));

        log.info("Categories:\n=====================================");
        for (String s : categories) {
            log.info(s);
        }
        log.info("DONE " + builders.size());
    }

    @Test
    public void testProperties() throws Exception {

        List<FilterBuilder> filterBuilders = scanner.getAvailableFilterBuilders();
        List<CategoryBuilder> categoryBuilders = scanner.getAvailableFilterCategories();

        GephiImporter gephiImporter = new GephiImporter(log, new ImportControllerImpl());
        WorkspaceWrapper ww = new GephiWorkspaceProvider().getWorkspace();
        gephiImporter.importGexf(IOUtil.readAsString(getClass().getClassLoader().getResourceAsStream("gexf/misserables.gexf")), ww);

        for (CategoryBuilder b : categoryBuilders) {
            filterBuilders.addAll(Arrays.asList(b.getBuilders()));
        }

        log.info("========================================");
        Set<Class<?>> classes = new HashSet<Class<?>>();

        for (FilterBuilder b : filterBuilders) {
            // log.info("Creating filter from builder: " + b);
            Filter filter = b.getFilter();

            if (filter.getProperties() == null) {
                continue;
            }
            for (FilterProperty fp : filter.getProperties()) {
                classes.add(fp.getValueType());

                if (fp.getValueType() == AttributeColumn.class) {
                    log.info(b.getClass().getSimpleName() + ":" + filter.getName() + " [ " + fp.getName() + ":" + (AttributeUtils.getDefault().isNodeColumn(((AttributeColumn) fp.getValue())) ? "NodeAttribute" : "EdgeAttribute") + "]");
                }

                if (fp.getValueType() == Range.class || fp.getValueType() == Number.class) {
                    log.info(b.getClass().getSimpleName() + ":" + filter.getName() + " [ " + fp.getName() + ":" + fp.getValueType().getSimpleName() + "]");
                }

            }
        }

        for (Class<?> c : classes) {
            log.info(c.getSimpleName() + " : " + c.getName());
        }

        log.info("DONE " + filterBuilders.size());
    }
}
