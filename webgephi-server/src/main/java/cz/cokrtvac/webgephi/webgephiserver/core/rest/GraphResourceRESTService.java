package cz.cokrtvac.webgephi.webgephiserver.core.rest;

import cz.cokrtvac.webgephi.api.model.GraphFunctionXml;
import cz.cokrtvac.webgephi.api.model.WebgephiWebException;
import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphsXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.api.model.ranking.RankingXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;
import cz.cokrtvac.webgephi.api.model.user.UserXml;
import cz.cokrtvac.webgephi.webgephiserver.core.WebgephiXmlFactory;
import cz.cokrtvac.webgephi.webgephiserver.core.ejb.GraphDAO;
import cz.cokrtvac.webgephi.webgephiserver.core.ejb.UserDAO;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.GephiExporter;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.GephiImporter;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.filter.GephiFilterProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.layout.GephiLayoutProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.ranking.GephiRankingProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.statistics.GephiStatisticsProcessor;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.function.statistics.StatisticsWrapper;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.GephiWorkspaceProvider;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.workspace.WorkspaceWrapper;
import cz.cokrtvac.webgephi.webgephiserver.core.rest.decorator.AddLinksEnhanced;
import cz.cokrtvac.webgephi.webgephiserver.core.rest.interceptor.WebException;
import cz.cokrtvac.webgephi.webgephiserver.core.util.HtmlUtils;
import cz.cokrtvac.webgephi.webgephiserver.core.util.security_annotation.Secure;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.GraphEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.UserEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role;
import org.jboss.resteasy.links.LinkResource;
import org.jboss.resteasy.links.LinkResources;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Resources over Graph - list of graphs, inserting new one, applying functions, ...
 */
@Path("/users/{user}/graphs")
@Stateless
@WebException
@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
public class GraphResourceRESTService {
    @Inject
    private Logger log;

    @Inject
    private GraphDAO graphDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private GephiLayoutProcessor layoutProcessor;

    @Inject
    private GephiStatisticsProcessor statisticsProcessor;

    @Inject
    private GephiRankingProcessor rankingProcessor;

    @Inject
    private GephiFilterProcessor filterProcessor;

    @Inject
    private GephiExporter gephiExporter;

    @Inject
    private GephiImporter gephiImporter;

    /**
     * One GephiWorkspaceProvider per Stateless ejb -> only one thread is accessing GephiWorkspaceProvider instance.
     * After every request, {@link GephiWorkspaceProvider}.clear() has to be called to clear workspace.
     */
    @Inject
    private GephiWorkspaceProvider gephiManager;

    /**
     * Pure gexf format of saved graph
     *
     * @param id
     * @return
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    @Secure(value = Role.GRAPHS_READ, owner = "#{arg1}")
    @LinkResource(value = GraphDetailXml.class, pathParameters = {"${owner}", "${id}"})
    @GET
    @Path("{id}/gexf")
    public String getGraphAsGexf(@Context HttpServletRequest req, @PathParam("user") String user, @PathParam("id") Long id) throws ParserConfigurationException, SAXException, IOException {
        GraphEntity e = getGraph(user, id);
        return e.getXml();

    }

    /**
     * Pure svg format of saved graph
     *
     * @param id
     * @return
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    @Secure(value = Role.GRAPHS_READ, owner = "#{arg1}")
    @LinkResource(value = GraphDetailXml.class, pathParameters = {"${owner}", "${id}"})
    @GET
    @Path("{id}/svg")
    @Produces("image/svg+xml")
    public /* synchronized*/ String getGraphAsSvg(@Context HttpServletRequest req, @PathParam("user") String user, @PathParam("id") Long id) throws ParserConfigurationException, SAXException, IOException {
        GraphEntity e = getGraph(user, id);

        String xml = e.getXml();
        try {
            WorkspaceWrapper ww = gephiManager.getWorkspace();
            return gephiExporter.toSvg(xml, ww);
        } finally {
            gephiManager.clear();
        }
    }

    /**
     * Statistics report, if last function was a statistic
     *
     * @param id
     * @return
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    @Secure(value = Role.GRAPHS_READ, owner = "#{arg1}")
    @LinkResource(value = GraphDetailXml.class, rel = GraphDetailXml.STATISTICS_REPORT, pathParameters = {"${owner}", "${id}"})
    @GET
    @Path("{id}/" + GraphDetailXml.STATISTICS_REPORT)
    @Produces({MediaType.TEXT_HTML})
    public String getGraphStatisticReport(@Context HttpServletRequest req, @PathParam("user") String user, @PathParam("id") Long id) throws ParserConfigurationException, SAXException, IOException {
        GraphEntity e = getGraph(user, id);
        if (e.getStatisticsReport() == null) {
            throw new WebgephiWebException(Status.NOT_FOUND, "This graph has no statistics", "Statistics are set only if some statistic function was applied previously");
        }
        return e.getStatisticsReport();

    }

    /**
     * Graph saved in db
     * With link to GEXF data
     *
     * @param id
     * @return
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    @Secure(value = Role.GRAPHS_READ, owner = "#{arg1}")
    @AddLinksEnhanced
    @LinkResource(value = GraphDetailXml.class, pathParameters = {"${owner}", "${id}"})
    @GET
    @Path("{id}")
    public GraphDetailXml getGraphDetail(@Context HttpServletRequest req, @PathParam("user") String user, @PathParam("id") Long id) throws ParserConfigurationException, SAXException, IOException {
        GraphEntity e = getGraph(user, id);
        GraphDetailXml xml = WebgephiXmlFactory.createXml(e);
        return xml;
    }

    /**
     * List of all graphs in db
     *
     * @return
     */
    @Secure(value = Role.GRAPHS_READ, owner = "#{arg1}")
    @AddLinksEnhanced
    @LinkResources({
            @LinkResource(value = GraphsXml.class, pathParameters = {"${owner}"}),
            @LinkResource(value = UserXml.class, rel = "graphs")
    })
    @GET
    public GraphsXml getAllGraphs(@Context HttpServletRequest req, @PathParam("user") String user, @QueryParam("page") Long page, @QueryParam("pageSize") Integer pageSize, @QueryParam("desc") boolean desc) {
        UserEntity userEntity = userDAO.getUserEntity(user);
        if (userEntity == null) {
            throw new WebgephiWebException(Status.NOT_FOUND, "User " + user + " does not exist");
        }

        if (page == null || page < 1) {
            page = 1l;
        }
        if (pageSize == null || pageSize < 1 || pageSize > 50) {
            pageSize = 50;
        }

        GraphsXml graphs = new GraphsXml();
        //long cnt = graphDAO.count(userEntity);
        long lastPage = (graphDAO.lastPage(userEntity, pageSize)) + 1;
        //log.trace("Cnt: " + cnt + ", Last page: " + lastPage);

        if (page > lastPage) {
            page = lastPage;
        }

        graphs.setAtomLinks(page, lastPage, pageSize == 50 ? null : pageSize, desc);
        graphs.setOwner(userDAO.get(user).getUsername());

        for (GraphEntity e : graphDAO.getPage(userDAO.getUserEntity(user), pageSize, page - 1, desc)) {
            GraphDetailXml xml = WebgephiXmlFactory.createXml(e);
            graphs.getGraphs().add(xml);
        }
        return graphs;
    }

    /**
     * Creates a new graph
     *
     * @param document
     * @param name
     * @return
     */
    @Secure(value = Role.GRAPHS_WRITE, owner = "#{arg2}")
    @POST
    @AddLinksEnhanced
    @LinkResource(value = GraphDetailXml.class, pathParameters = {"${owner}"})
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN})
    public Response addGraph(@Context HttpServletRequest req, String document, @PathParam("user") String user, @QueryParam("name") String name, @QueryParam("format") String format) {
        log.debug("POSTing GEXF name=" + name + ", format=" + format);

        String importedGraph;
        try {
            WorkspaceWrapper ww = gephiManager.getWorkspace();
            gephiImporter.importGraph(document, format, ww);
            importedGraph = gephiExporter.toGexf(ww);
        } catch (Exception e) {
            gephiManager.clear();
            String m = "Input graph is invalid";
            if (format != null) {
                m += " '" + format + "' format. You can let WebGephi try to auto-resolve format (by not specifying format parameter).";
            } else {
                m += ". Auto-resolve of graph format probably failed, try to specify format using 'format' parameter";
            }
            throw new WebgephiWebException(Status.CONFLICT, m, e);
        } finally {
            gephiManager.clear();
        }

        GraphEntity graph = new GraphEntity();
        graph.setName(name);
        graph.setXml(importedGraph);
        graph.setOwner(userDAO.getUserEntity(user));
        graphDAO.persist(graph);

        GraphDetailXml resXml = WebgephiXmlFactory.createXml(graph);
        return Response.status(Status.CREATED).entity(resXml).build();
    }

	/* FUNCTIONS ====================================================================================================================================== */

    /**
     * Applies layout function defined in xml content and
     * saves result to DB
     *
     * @param id
     * @return Result graph - with link to GEXF data
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    @Secure(value = Role.GRAPHS_WRITE, owner = "#{arg2}")
    @AddLinksEnhanced
    @LinkResource(value = GraphDetailXml.class, rel = GraphDetailXml.APPLY_FUNCTION, pathParameters = {"${owner}", "${id}"})
    @PUT
    @Path("{id}")
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    public Response applyFunction(@Context HttpServletRequest req, GraphFunctionXml function, @PathParam("user") String user, @PathParam("id") Long id, @QueryParam("name") String name, @QueryParam("repeat") Integer repeat) {
        try {
            if (log.isDebugEnabled()) {
                StringWriter sw = new StringWriter();
                JAXBContext.newInstance(GraphFunctionXml.class).createMarshaller().marshal(function, sw);
                log.debug("Request body:\n" + sw.toString());
                sw.close();
            }

            GraphDetailXml xml = null;
            if (function.getFunction() instanceof LayoutXml) {
                xml = applyLayout(req, (LayoutXml) function.getFunction(), user, id, name, repeat);
            } else if (function.getFunction() instanceof StatisticXml) {
                xml = applyStatistics(req, (StatisticXml) function.getFunction(), user, id, name);
            } else if (function.getFunction() instanceof RankingXml) {
                xml = applyRanking(req, (RankingXml) function.getFunction(), user, id, name);
            } else if (function.getFunction() instanceof FilterXml) {
                xml = applyFilter(req, (FilterXml) function.getFunction(), user, id, name);
            } else {
                throw new IllegalArgumentException("Function not implemented.");
            }
            return Response.status(Status.CREATED).entity(xml).build();
        } catch (WebgephiWebException we) {
            throw we;
        } catch (Exception e) {
            throw new WebgephiWebException(Status.CONFLICT, "Function could not be applied", e);
        }
    }

    /* LAYOUT ====================================================================================================================================== */
    private GraphDetailXml applyLayout(HttpServletRequest req, LayoutXml layout, String user, Long id, String newName, Integer repeat)
            throws ParserConfigurationException, SAXException, IOException {

        GraphEntity e = getGraph(user, id);
        log.debug("Layout: " + layout.toString());
        log.debug("Repeat: " + repeat);
        if (repeat == null) {
            repeat = 1;
        }

        try {
            WorkspaceWrapper ww = gephiManager.getWorkspace();
            gephiImporter.importGexf(e.getXml(), ww);

            layoutProcessor.process(ww, layout, repeat);
            String resXml = gephiExporter.toGexf(ww);

            GraphEntity result = new GraphEntity();
            if (newName != null) {
                result.setName(newName);
            } else {
                result.setName(e.getName() + "_" + layout.getId());
            }
            result.setParent(e);
            result.setXml(resXml);
            result.setStatisticsReport(e.getStatisticsReport());
            result.setOwner(e.getOwner());
            graphDAO.persist(result);

            GraphDetailXml xml = WebgephiXmlFactory.createXml(result);

            return xml;
        } finally {
            gephiManager.clear();
        }
    }

	/* STATISTICS ====================================================================================================================================== */

    private GraphDetailXml applyStatistics(@Context HttpServletRequest req, StatisticXml statisticXml, @PathParam("user") String user, @PathParam("id") Long id, String newName) throws Exception,
            SAXException, IOException {

        GraphEntity e = getGraph(user, id);
        log.debug("Statistic: " + statisticXml.toString());
        if (log.isDebugEnabled()) {
            StringWriter sw = new StringWriter();
            JAXBContext.newInstance(GraphFunctionXml.class).createMarshaller().marshal(statisticXml, sw);
            log.debug("Request body:\n" + sw.toString());
            sw.close();
        }

        WorkspaceWrapper ww = gephiManager.getWorkspace();
        gephiImporter.importGexf(e.getXml(), ww);

        try {
            StatisticsWrapper sw = statisticsProcessor.process(ww, statisticXml, null);
            String resXml = gephiExporter.toGexf(ww);

            GraphEntity result = new GraphEntity();

            if (newName != null) {
                result.setName(newName);
            } else {
                result.setName(e.getName() + "_" + statisticXml.getId());
            }
            result.setParent(e);
            result.setOwner(e.getOwner());
            result.setXml(resXml);

            String report = sw.getReport();
            try {
                report = HtmlUtils.embededImages(report);
            } catch (Exception ee) {
                log.error("Images could not be embeded.", ee);
            }

            result.setStatisticsReport(report);
            graphDAO.persist(result);

            GraphDetailXml xml = WebgephiXmlFactory.createXml(result);

            return xml;
        } finally {
            gephiManager.clear();
        }
    }

    /* RANKING ====================================================================================================================================== */

    private GraphDetailXml applyRanking(@Context HttpServletRequest req, RankingXml rankingXml, @PathParam("user") String user, @PathParam("id") Long id, String newName) throws ParserConfigurationException,
            SAXException, IOException {

        GraphEntity e = getGraph(user, id);
        log.debug("Ranking: " + rankingXml.toString());

        WorkspaceWrapper ww = gephiManager.getWorkspace();
        gephiImporter.importGexf(e.getXml(), ww);

        try {
            rankingProcessor.process(ww, rankingXml, null);
            String resXml = gephiExporter.toGexf(ww);

            GraphEntity result = new GraphEntity();

            if (newName != null) {
                result.setName(newName);
            } else {
                result.setName(e.getName() + "_" + rankingXml.getId());
            }
            result.setParent(e);
            result.setOwner(e.getOwner());
            result.setXml(resXml);
            graphDAO.persist(result);

            GraphDetailXml xml = WebgephiXmlFactory.createXml(result);

            return xml;
        } finally {
            gephiManager.clear();
        }
    }

     /* FILTER ====================================================================================================================================== */

    private GraphDetailXml applyFilter(@Context HttpServletRequest req, FilterXml filterXml, @PathParam("user") String user, @PathParam("id") Long id, String newName) throws ParserConfigurationException,
            SAXException, IOException {

        GraphEntity e = getGraph(user, id);
        log.debug("Filter: " + filterXml.toString());

        WorkspaceWrapper ww = gephiManager.getWorkspace();
        gephiImporter.importGexf(e.getXml(), ww);

        try {
            filterProcessor.process(ww, filterXml, null);
            String resXml = gephiExporter.toGexf(ww);

            GraphEntity result = new GraphEntity();

            if (newName != null) {
                result.setName(newName);
            } else {
                result.setName(e.getName() + "_" + filterXml.getId());
            }
            result.setParent(e);
            result.setOwner(e.getOwner());
            result.setXml(resXml);
            graphDAO.persist(result);

            GraphDetailXml xml = WebgephiXmlFactory.createXml(result);

            return xml;
        } finally {
            gephiManager.clear();
        }
    }

    private GraphEntity getGraph(String username, long graphId) {
        GraphEntity e = graphDAO.get(graphId);
        if (e == null) {
            throw new WebgephiWebException(Status.NOT_FOUND, "Graph with id " + graphId + " does not exist");
        }
        if (!e.getOwner().getUsername().equals(username)) {
            throw new WebgephiWebException(Status.NOT_FOUND, "Graph with id " + graphId + " does not exist");
        }
        return e;
    }
}
