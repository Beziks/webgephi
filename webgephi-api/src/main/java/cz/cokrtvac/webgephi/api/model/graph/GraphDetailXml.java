package cz.cokrtvac.webgephi.api.model.graph;

import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 16:43
 */
@XmlRootElement(name = "graph")
@XmlAccessorType(XmlAccessType.NONE)
public class GraphDetailXml {
    public static final String STATISTICS_REPORT = "statistics-report";
    public static final String APPLY_FUNCTION = "apply-function";

    private String name;
    private GraphDetailXml parent;
    private Long id;
    private Date created;

    public boolean hasStatistics() {
        return restServiceDiscovery.getLinkForRel(GraphDetailXml.STATISTICS_REPORT) != null;
    }

    @XmlAttribute
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @XmlElement
    public GraphDetailXml getParent() {
        return parent;
    }

    public void setParent(GraphDetailXml parent) {
        this.parent = parent;
    }

    @XmlElementRef
    public RESTServiceDiscovery restServiceDiscovery = new RESTServiceDiscovery();

    private String owner;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "GraphDetailXml{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", parent=" + parent +
                ", hasStatistics=" + hasStatistics() +
                '}';
    }
}
