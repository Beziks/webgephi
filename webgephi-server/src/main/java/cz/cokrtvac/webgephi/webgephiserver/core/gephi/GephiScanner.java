package cz.cokrtvac.webgephi.webgephiserver.core.gephi;

import cz.cokrtvac.webgephi.webgephiserver.core.InitializationException;
import org.gephi.filters.spi.CategoryBuilder;
import org.gephi.filters.spi.FilterBuilder;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.reflections.Reflections;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 3.6.13
 * Time: 14:26
 */
public class GephiScanner {

    private Logger log;
    private GephiImporter gephiImporter;

    @Inject
    public GephiScanner(Logger log, GephiImporter gephiImporter) {
        this();
        this.log = log;
        this.gephiImporter = gephiImporter;
    }

    //private Reflections reflections = new Reflections(ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader());
    private Reflections reflections = new Reflections("");

    public GephiScanner() {
       /* List<ClassLoader> classLoadersList = new ArrayList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());
        classLoadersList.add(FilterBuilder.class.getClassLoader());

        this.reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(true), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new org.reflections.util.FilterBuilder().include(""))
        ); */


    }

    public List<Layout> getAvailableLayouts() {
        List<Layout> out = new ArrayList<Layout>();

        Set<Class<? extends LayoutBuilder>> layoutBuilders = reflections.getSubTypesOf(LayoutBuilder.class);

        for (Class<?> c : layoutBuilders) {
            try {
                if (LayoutBuilder.class.isAssignableFrom(c)) {
                    LayoutBuilder builder = (LayoutBuilder) c.newInstance();
                    if (builder.getName().equalsIgnoreCase("test")) {
                        // Skip test layout
                        continue;
                    }
                    Layout layout = builder.buildLayout();
                    layout.resetPropertiesValues();
                    out.add(layout);
                }
            } catch (Exception e) {
                String msg = "Error during scanning GEPHI LAYOUTs";
                log.error(msg, e);
                throw new InitializationException(e);
            }
        }
        return out;
    }

    public List<StatisticsBuilder> getAvailableStatistics() {
        Set<Class<? extends StatisticsBuilder>> sbs = reflections.getSubTypesOf(StatisticsBuilder.class);
        List<StatisticsBuilder> out = new ArrayList<StatisticsBuilder>();
        for (Class<? extends StatisticsBuilder> b : sbs) {
            try {
                out.add(b.newInstance());
            } catch (Exception e) {
                String msg = "Error during scanning GEPHI STATISTICs";
                log.error(msg, e);
                throw new InitializationException(e);
            }
        }


        return out;
    }

    public List<Class<? extends FilterBuilder>> getAvailableFilters() {
        List<Class<? extends FilterBuilder>> out = new ArrayList<Class<? extends FilterBuilder>>();
        Set<Class<? extends FilterBuilder>> builders = reflections.getSubTypesOf(FilterBuilder.class);

        for (Class<?> c : builders) {
            if (Modifier.isAbstract(c.getModifiers()) || c.isInterface()) {
                continue;
            }
            if (c.getName().contains("DynamicRangeFilterBuilder")) {
                // Skip Time Interval filter
                continue;
            }
            out.add((Class<FilterBuilder>) c);
        }

        return out;
    }

    public List<FilterBuilder> getAvailableFilterBuilders() {
        List<FilterBuilder> out = new ArrayList<FilterBuilder>();
        Set<Class<? extends FilterBuilder>> builders = reflections.getSubTypesOf(FilterBuilder.class);

        for (Class<?> c : builders) {
            if (Modifier.isAbstract(c.getModifiers()) || c.isInterface()) {
                continue;
            }
            try {
                FilterBuilder builder = null;
                try {
                    builder = (FilterBuilder) c.newInstance();
                } catch (Exception e) {
                    log.info("Filter builder " + c.getSimpleName() + " cannot be instantialized, category builder have to be used (probably)");
                    continue;
                }
                out.add(builder);
                log.info("Found Filter builder " + c.getSimpleName() + "(" + builder.getCategory().getName() + ":" + builder.getName() + ", " + builder.getDescription() + ", " + ")");
            } catch (Exception e) {
                String msg = "Error during scanning GEPHI FILTERs: " + c.getSimpleName();
                log.error(msg, e);
                throw new InitializationException(e);
            }
        }

        return out;
    }

    public List<CategoryBuilder> getAvailableFilterCategories() {
        List<CategoryBuilder> out = new ArrayList<CategoryBuilder>();
        Set<Class<? extends CategoryBuilder>> builders = reflections.getSubTypesOf(CategoryBuilder.class);

        for (Class<?> c : builders) {
            if (Modifier.isAbstract(c.getModifiers()) || c.isInterface()) {
                continue;
            }
            try {
                CategoryBuilder builder = (CategoryBuilder) c.newInstance();
                out.add(builder);
                log.info("Found Filter Category builder " + c.getSimpleName() + "(" + builder.getCategory().getName() + ")");
            } catch (Exception e) {
                String msg = "Error during scanning GEPHI FILTER CATEGORies: " + c.getSimpleName();
                log.error(msg, e);
                throw new InitializationException(e);
            }
        }

        return out;
    }
}
