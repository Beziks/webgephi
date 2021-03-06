package cz.cokrtvac.webgephi.webgephiserver.core.rest.decorator;

import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphsXml;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.links.RESTServiceDiscovery.AtomLink;
import org.jboss.resteasy.links.impl.RESTUtils;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.DecoratorProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Marshaller.Listener;
import java.lang.annotation.Annotation;

public class EnhancedLinkDecorator implements DecoratorProcessor<Marshaller, AddLinksEnhanced> {
    private static Logger log = LoggerFactory.getLogger(EnhancedLinkDecorator.class);

    public Marshaller decorate(Marshaller target, final AddLinksEnhanced annotation, Class type, Annotation[] annotations, MediaType mediaType) {
        target.setListener(new Listener() {
            @Override
            public void beforeMarshal(Object entity) {
                UriInfo uriInfo = ResteasyProviderFactory.getContextData(UriInfo.class);
                ResourceMethodRegistry registry = (ResourceMethodRegistry) ResteasyProviderFactory.getContextData(Registry.class);

                boolean removeStatLink = false;
                if (entity instanceof GraphDetailXml) {
                    GraphDetailXml xml = (GraphDetailXml) entity;
                    AtomLink stat = xml.restServiceDiscovery.getLinkForRel(GraphDetailXml.STATISTICS_REPORT);
                    removeStatLink = stat == null;
                    xml.restServiceDiscovery.remove(stat);
                }

                // find all rest service classes and scan them
                RESTUtils.addDiscovery(entity, uriInfo, registry);

                if (entity instanceof AtomLink) {
                    updateLinksType((AtomLink) entity);
                }
                if (entity instanceof GraphDetailXml) {
                    updateGraphDetailXml((GraphDetailXml) entity, removeStatLink);
                }
                if (entity instanceof GraphsXml) {
                    updateGraphsAtomLinks((GraphsXml) entity);
                }
            }

        });
        return target;
    }

    private void updateLinksType(AtomLink link) {
        if (link.getHref().endsWith("gexf")) {
            link.setType(MediaType.APPLICATION_XML);
        } else if (link.getHref().endsWith("svg")) {
            link.setType("image/svg+xml");
        } else if (link.getHref().endsWith(GraphDetailXml.STATISTICS_REPORT)) {
            link.setType(MediaType.TEXT_HTML);
        } else {
            link.setType(MediaType.TEXT_XML);
        }
    }

    private void updateGraphDetailXml(GraphDetailXml graphDetailXml, boolean removeStatLink) {
        if(removeStatLink){
            AtomLink link = graphDetailXml.restServiceDiscovery.getLinkForRel(GraphDetailXml.STATISTICS_REPORT);
            graphDetailXml.restServiceDiscovery.remove(link);
        }
    }

    private void updateGraphsAtomLinks(GraphsXml graphsXml) {
        graphsXml.updateAtomLinksHref();
        graphsXml.getRestServiceDiscovery().clear();
    }
}
