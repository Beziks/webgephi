package cz.cokrtvac.webgephi.webgephiserver.core.gephi.layout;

import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutsXml;
import cz.cokrtvac.webgephi.webgephiserver.core.WebgephiXmlFactory;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.GephiScanner;
import org.gephi.layout.spi.Layout;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.*;

/**
 * Provides info about all available layout functions in application.
 * Runs after deploy.
 * <p/>
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 15:18
 */
@Singleton
@Startup
public class LayoutsPool {

    @Inject
    private Logger log;

    @Inject
    private GephiScanner gephiScanner;

    private LayoutsXml layouts;
    private Map<String, LayoutXml> layoutXmlsMap = new HashMap<String, LayoutXml>();
    private Map<String, Layout> layoutsMap = new HashMap<String, Layout>();

    @PostConstruct
    public void init() {
        log.info("Initializing app setting.");

        initMaps();
        initLayouts();
        log.info("Initializing done.");
    }

    public LayoutsXml getAvailableLayouts() {
        return layouts;
    }

    public Layout getNewLayout(String id) {
        Layout l = layoutsMap.get(id);
        l = l.getBuilder().buildLayout();
        l.resetPropertiesValues();
        return l;
    }

    private void initMaps() {
        List<Layout> availableLayouts = gephiScanner.getAvailableLayouts();
        log.debug("Available layouts size: " + availableLayouts.size());

        for (Layout l : availableLayouts) {
            LayoutXml layoutXml = WebgephiXmlFactory.create(l);

            layoutsMap.put(layoutXml.getId(), l);
            layoutXmlsMap.put(layoutXml.getId(), layoutXml);
        }
    }

    private void initLayouts() {
        List<LayoutXml> all = new ArrayList<LayoutXml>(layoutXmlsMap.values());
        Collections.sort(all, new Comparator<LayoutXml>() {
            @Override
            public int compare(LayoutXml o1, LayoutXml o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        layouts = new LayoutsXml();
        layouts.getLayouts().addAll(all);
    }
}
