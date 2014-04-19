package cz.cokrtvac.webgephi.webgephiserver.core.gephi;

import cz.cokrtvac.webgephi.webgephiserver.core.InitializationException;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.reflections.Reflections;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 3.6.13
 * Time: 14:26
 */
public class GephiScanner {
    @Inject
    private Logger log;

    private Reflections reflections = new Reflections("org.gephi");

    public List<Layout> getAvailableLayouts() {
        List<Layout> out = new ArrayList<Layout>();

        Set<Class<? extends LayoutBuilder>> layoutBuilders = reflections.getSubTypesOf(LayoutBuilder.class);

        for (Class<?> c : layoutBuilders) {
            try {
                if (LayoutBuilder.class.isAssignableFrom(c)) {
                    LayoutBuilder builder = (LayoutBuilder) c.newInstance();
                    if(builder.getName().equalsIgnoreCase("test")){
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
}
