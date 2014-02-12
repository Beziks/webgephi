package cz.cokrtvac.webgephi.webgephiserver.core.rest;

import cz.cokrtvac.webgephi.webgephiserver.core.gephi.layout.LayoutsPool;
import cz.cokrtvac.webgephi.api.model.WebgephiWebException;
import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutsXml;
import cz.cokrtvac.webgephi.webgephiserver.core.rest.interceptor.WebException;
import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.links.LinkResource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * List of available layouts
 */
@Path("/layouts")
@Stateless
@WebException
@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
public class LayoutResourceRESTService {
    @Inject
    private LayoutsPool layoutsPool;

    @AddLinks
    @LinkResource
    @GET
    public LayoutsXml listAllLayouts(@Context HttpServletRequest req) {
        LayoutsXml layoutsXml = layoutsPool.getAvailableLayouts();
        return layoutsXml;
    }

    @AddLinks
    @LinkResource
    @GET
    @Path("/{id}")
    public LayoutXml getLayout(@Context HttpServletRequest req, @PathParam("id") String id) {
        for (LayoutXml l : layoutsPool.getAvailableLayouts().getLayouts()) {
            if (l.getId().equals(id)) {
                return l;
            }
        }
        throw new WebgephiWebException(Response.Status.NOT_FOUND, "Lazout function " + id + " does not exist");
    }
}
