package cz.cokrtvac.webgephi.webgephiserver.core.rest;

import cz.cokrtvac.webgephi.api.model.WebgephiWebException;
import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.api.model.filter.FiltersXml;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.FiltersPool;
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
 * List of available filters
 */
@Path("/filters")
@Stateless
@WebException
@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
public class FiltersResourceRESTService {
    @Inject
    private FiltersPool filtersPool;

    @AddLinks
    @LinkResource
    @GET
    public FiltersXml listAllFilters(@Context HttpServletRequest req) {
        FiltersXml filtersXml = filtersPool.getAll();
        return filtersXml;
    }

    @AddLinks
    @LinkResource
    @GET
    @Path("/{id}")
    public FilterXml getStatistic(@Context HttpServletRequest req, @PathParam("id") String id) {
        for (FilterXml s : filtersPool.getAll().getFunctions()) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        throw new WebgephiWebException(Response.Status.NOT_FOUND, "Filter function " + id + " does not exist");
    }
}
