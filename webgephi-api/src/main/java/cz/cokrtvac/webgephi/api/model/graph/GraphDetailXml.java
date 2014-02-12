package cz.cokrtvac.webgephi.api.model.graph;

import cz.cokrtvac.webgephi.api.model.HasId;
import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.*;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 16:43
 */
@XmlRootElement(name = "graph")
@XmlAccessorType(XmlAccessType.NONE)
public class GraphDetailXml implements HasId {
    public static final String STATISTICS_REPORT = "statistics-report";
    public static final String APPLY_FUNCTION = "apply-function";

    private String name;
    private GraphDetailXml parent;
    private String id;
    private boolean hasStatistics = false;

    public boolean hasStatistics() {
        return hasStatistics;
    }

    public void setHasStatistics(boolean hasStatistics) {
        this.hasStatistics = hasStatistics;
    }

    @XmlID
    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public GraphDetailXml getParent() {
        return parent;
    }

    public void setParent(GraphDetailXml parent) {
        this.parent = parent;
    }

    @XmlElementRef
    public RESTServiceDiscovery restServiceDiscovery;

    private String owner;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
