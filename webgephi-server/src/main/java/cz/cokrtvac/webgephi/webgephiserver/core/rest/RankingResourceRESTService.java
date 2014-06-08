package cz.cokrtvac.webgephi.webgephiserver.core.rest;

import cz.cokrtvac.webgephi.api.model.WebgephiWebException;
import cz.cokrtvac.webgephi.api.model.ranking.RankingXml;
import cz.cokrtvac.webgephi.api.model.ranking.RankingsXml;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.ranking.RankingsPool;
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
@Path("/rankings")
@Stateless
@WebException
@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
public class RankingResourceRESTService {
    @Inject
    private RankingsPool rankingsPool;

    @AddLinks
    @LinkResource
    @GET
    public RankingsXml listAllLayouts(@Context HttpServletRequest req) {
        RankingsXml rankingsXml = rankingsPool.getAll();
        return rankingsXml;
    }

    @AddLinks
    @LinkResource
    @GET
    @Path("/{id}")
    public RankingXml getLayout(@Context HttpServletRequest req, @PathParam("id") String id) {
        for (RankingXml r : rankingsPool.getAll().getFunctions()) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        throw new WebgephiWebException(Response.Status.NOT_FOUND, "Ranking function " + id + " does not exist");
    }
}
