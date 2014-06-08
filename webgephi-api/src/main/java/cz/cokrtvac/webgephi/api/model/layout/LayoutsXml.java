package cz.cokrtvac.webgephi.api.model.layout;

import cz.cokrtvac.webgephi.api.model.AbstractFunctionsXml;
import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 10:35
 */
@XmlRootElement(name = "layouts")
@XmlAccessorType(XmlAccessType.NONE)
public class LayoutsXml extends AbstractFunctionsXml<LayoutXml> {

    @Override
    @XmlElement(name = "layout")
    public List<LayoutXml> getFunctions() {
        return super.getFunctions();
    }

    @Override
    public void setFunctions(List<LayoutXml> functions) {
        super.setFunctions(functions);
    }

    @XmlElementRef
    private RESTServiceDiscovery restServiceDiscovery;
}
