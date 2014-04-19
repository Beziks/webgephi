package cz.cokrtvac.webgephi.api.model.graph;

import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.RESTServiceDiscovery.AtomLink;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 19:52
 */
@XmlRootElement(name = GraphsXml.PATH)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"restServiceDiscovery", "first", "prev", "self", "next", "last", "graphs"})
public class GraphsXml {
    public static final String PATH = "graphs";

    private List<GraphDetailXml> graphs;

    private AtomLink first;
    private AtomLink prev;
    private AtomLink self;
    private AtomLink next;
    private AtomLink last;

    private List<AtomLink> links = new ArrayList<AtomLink>();

    public void setAtomLinks(long selfPage, long lastPage, Integer pageSize, boolean desc) {
        String pagesizeString = "";
        if (pageSize != null) {
            pagesizeString = "&pageSize=" + pageSize;
        }

        if(desc){
            pagesizeString += "&desc=true";
        }

        first = new AtomLink("?page=1" + pagesizeString, "first");
        links.add(first);
        if (selfPage > 1) {
            prev = new AtomLink("?page=" + (selfPage - 1) + pagesizeString, "prev");
            links.add(prev);
        }
        self = new AtomLink("?page=" + (selfPage) + pagesizeString, "self");
        links.add(self);
        if (selfPage < lastPage) {
            next = new AtomLink("?page=" + (selfPage + 1) + pagesizeString, "next");
            links.add(next);
        }
        last = new AtomLink("?page=" + lastPage + pagesizeString, "last");
        links.add(last);
    }

    public void updateAtomLinksHref() {
        String base = restServiceDiscovery.get(0).getHref();
        for (AtomLink l : links) {
            if (l != null && !l.getHref().startsWith(base)) {
                l.setHref(base + l.getHref());
            }
        }

    }

    @XmlElementRef
    public AtomLink getFirst() {
        if(first == null){
            first = getLink("first");
        }
        return first;
    }

    public void setFirst(AtomLink first) {
        this.first = first;
    }

    @XmlElementRef
    public AtomLink getPrev() {
        if(prev == null){
            prev = getLink("prev");
        }
        return prev;
    }

    public void setPrev(AtomLink prev) {
        this.prev = prev;
    }

    @XmlElementRef
    public AtomLink getSelf() {
        if(self == null){
            self = getLink("self");
        }
        return self;
    }

    public void setSelf(AtomLink self) {
        this.self = self;
    }

    @XmlElementRef
    public AtomLink getNext() {
        if(next == null){
            next = getLink("next");
        }
        return next;
    }

    public void setNext(AtomLink next) {
        this.next = next;
    }

    @XmlElementRef
    public AtomLink getLast() {
        if(last == null){
            last = getLink("last");
        }
        return last;
    }

    public void setLast(AtomLink last) {
        this.last = last;
    }

    @XmlElement(name = "graph")
    public List<GraphDetailXml> getGraphs() {
        if (graphs == null) {
            graphs = new ArrayList<GraphDetailXml>();
        }
        return graphs;
    }

    public void setGraphs(List<GraphDetailXml> graphs) {
        this.graphs = graphs;
    }

    @XmlElementRef
    private RESTServiceDiscovery restServiceDiscovery;

    public RESTServiceDiscovery getRestServiceDiscovery() {
        return restServiceDiscovery;
    }

    private String owner;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "GraphsXml{" +
                "graphs=" + graphs +
                ", owner='" + owner + '\'' +
                '}';
    }

    private AtomLink getLink(String rel){
        if(restServiceDiscovery == null){
            return null;
        }
        return restServiceDiscovery.getLinkForRel(rel);
    }
}
