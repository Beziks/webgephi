package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.layout;

import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutsXml;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.GephiScanner;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.FunctionsPool;
import org.gephi.layout.spi.Layout;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.List;

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
public class LayoutsPool extends FunctionsPool<LayoutWrapper, LayoutXml, LayoutsXml> {

    @Inject
    private Logger log;

    @Inject
    private GephiScanner gephiScanner;
    private LayoutsXml layoutsXml;

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    protected void initMaps() {
        List<Layout> availableLayouts = gephiScanner.getAvailableLayouts();
        log.debug("Available layouts size: " + availableLayouts.size());

        for (Layout l : availableLayouts) {
            LayoutWrapper wrapper = new LayoutWrapper(l);
            functionMap.put(wrapper.getId(), wrapper);
            xmlMap.put(wrapper.getId(), create(wrapper));
        }
    }

    @Override
    protected LayoutXml createNew() {
        return new LayoutXml();
    }

    @Override
    public LayoutsXml createNewContainer() {
        return new LayoutsXml();
    }
}
