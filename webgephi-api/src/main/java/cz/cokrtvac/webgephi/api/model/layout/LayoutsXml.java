package cz.cokrtvac.webgephi.api.model.layout;

import cz.cokrtvac.webgephi.api.util.CollectionsUtil;
import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 10:35
 */
@XmlRootElement(name = LayoutsXml.PATH)
@XmlAccessorType(XmlAccessType.NONE)
public class LayoutsXml {
    public static final String PATH = "layouts";
    private List<LayoutXml> layouts;

    @XmlElement(name = "layout")
    public List<LayoutXml> getLayouts() {
        if (layouts == null) {
            layouts = new ArrayList<LayoutXml>();
        }
        return layouts;
    }

    public void setLayouts(List<LayoutXml> layouts) {
        this.layouts = layouts;
    }

    @Override
    public String toString() {
        return "LayoutsXml " + CollectionsUtil.toString(getLayouts());
    }

    @XmlElementRef
    private RESTServiceDiscovery restServiceDiscovery;
}
