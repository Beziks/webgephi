package cz.cokrtvac.webgephi.webgephiserver.core.rest;

import cz.cokrtvac.webgephi.api.model.WebgephiWebException;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticsXml;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.statistics.StatisticsPool;
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
@Path("/statistics")
@Stateless
@WebException
@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
public class StatisticsResourceRESTService {
    @Inject
    private StatisticsPool statisticsPool;

    @AddLinks
    @LinkResource
    @GET
    public StatisticsXml listAllStatistics(@Context HttpServletRequest req) {
        StatisticsXml statisticsXml = statisticsPool.getAll();
        return statisticsXml;
    }

    @AddLinks
    @LinkResource
    @GET
    @Path("/{id}")
    public StatisticXml getStatistic(@Context HttpServletRequest req, @PathParam("id") String id) {
        for (StatisticXml s : statisticsPool.getAll().getFunctions()) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        throw new WebgephiWebException(Response.Status.NOT_FOUND, "Statistic function " + id + " does not exist");
    }
}
