package cz.cokrtvac.webgephi.webgephiserver.core.gephi.function;

import cz.cokrtvac.webgephi.api.model.AbstractFunctionXml;
import cz.cokrtvac.webgephi.api.model.AbstractFunctionsXml;
import cz.cokrtvac.webgephi.webgephiserver.core.WebgephiXmlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 17. 5. 2014
 * Time: 17:27
 */
public abstract class FunctionsPool<F extends GraphFunction, X extends AbstractFunctionXml, ALL extends AbstractFunctionsXml<X>> {
    private Logger log = LoggerFactory.getLogger(getClass());

    protected ALL functionsXml;
    protected Map<String, F> functionMap = new HashMap<String, F>();
    protected Map<String, X> xmlMap = new HashMap<String, X>();

    public void init() {
        try {
            log.info("Initializing app setting.");
            initMaps();
            functionsXml = initXmlFunctions(xmlMap.values());
            log.info("Initializing done.");
        } catch (RuntimeException e) {
            log.error("Initialization of pool " + getClass().getSimpleName() + " failed: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Have to initialize functionMap and xmlMap
     */
    protected abstract void initMaps();

    protected ALL initXmlFunctions(Collection<X> functions) {
        ALL a = createNewContainer();
        List<X> all = new ArrayList<X>(functions);
        Collections.sort(all, new Comparator<X>() {
            @Override
            public int compare(X o1, X o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        a.setFunctions(all);
        return a;
    }

    protected abstract X createNew();

    public F createNew(X xml) {
        GraphFunction orig = functionMap.get(xml.getId());
        return (F) orig.copy();
    }

    public abstract ALL createNewContainer();

    public ALL getAll() {
        return functionsXml;
    }

    protected X create(F function) {
        X xml = createNew();
        xml.setId(function.getId());
        xml.setName(function.getName());
        xml.setDescription(function.getDescription());
        for (GraphFunctionProperty<?> p : function.getProperties()) {
            xml.addProperty(WebgephiXmlFactory.createXml(p));
        }
        return xml;
    }
}
