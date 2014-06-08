package cz.cokrtvac.webgephi.api.model.filter;

import cz.cokrtvac.webgephi.api.model.AbstractFunctionsXml;
import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 10:35
 */
@XmlRootElement(name = "filters")
@XmlAccessorType(XmlAccessType.NONE)
public class FiltersXml extends AbstractFunctionsXml<FilterXml> {
    private List<FilterXml> filters;

    @Override
    @XmlElement(name = "filter")
    public List<FilterXml> getFunctions() {
        return super.getFunctions();
    }

    @Override
    public void setFunctions(List<FilterXml> functions) {
        super.setFunctions(functions);
    }

    @XmlElementRef
    private RESTServiceDiscovery restServiceDiscovery;
}
