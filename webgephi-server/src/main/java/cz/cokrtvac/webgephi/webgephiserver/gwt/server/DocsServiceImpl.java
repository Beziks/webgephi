package cz.cokrtvac.webgephi.webgephiserver.gwt.server;

import cz.cokrtvac.webgephi.api.util.IOUtil;
import cz.cokrtvac.webgephi.api.util.XmlUtil;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.DocsService;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.docs.rest.RestDescription;
import org.jboss.errai.bus.server.annotations.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocsServiceImpl implements DocsService {

    @Override
    public List<RestDescription> getRestDescriptions() {
        List<RestDescription> all = new ArrayList<RestDescription>();
        // Layouts ============================================================
        all.add(new RestDescription(
                "Layout functions",
                "List of all available layout functions",
                "GET",
                "/layouts",
                "no",
                "GET /layouts",
                loadAndPrettify("/restSampleData/layouts_get_response.xml")));

        all.add(new RestDescription(
                "Layout function",
                "Shows one selected layout function",
                "GET",
                "/layouts/{layout_id}",
                "no",
                "GET /layouts/clockwise-rotate",
                loadAndPrettify("/restSampleData/layout_get_response.xml")));
        // Statistics ============================================================
        all.add(new RestDescription(
                "Statistic functions",
                "List of all available statistic functions",
                "GET",
                "/statistics",
                "no",
                "GET /statistics",
                loadAndPrettify("/restSampleData/statistics_get_response.xml")));

        all.add(new RestDescription(
                "Statistic function",
                "Shows one selected statistic function",
                "GET",
                "/statistics/{statistic_id}",
                "no",
                "GET /statistics/clustering-coefficient",
                loadAndPrettify("/restSampleData/statistic_get_response.xml")));
        // Users ============================================================
        all.add(new RestDescription(
                "List users",
                "List of all registered users. Is available only for admins.",
                "GET",
                "/users",
                "BASIC ('ADMIN' permission)",
                "GET /users",
                loadAndPrettify("/restSampleData/users_get_response.xml")));

        all.add(new RestDescription(
                "Add user",
                "Creates new regular user account. Anybody can do that.",
                "POST",
                "/users",
                "no",
                "POST /users",
                loadAndPrettify("/restSampleData/user_post_request.xml"),
                loadAndPrettify("/restSampleData/user_post_response.xml")));

        all.add(new RestDescription(
                "Show user",
                "Shows user profile (without password)",
                "GET",
                "/users/{username}",
                "BASIC, OAUTH ('PROFILE_READ' permission)",
                "get /users/test",
                loadAndPrettify("/restSampleData/user_get_response.xml")));

        all.add(new RestDescription(
                "Update user",
                "Updates user data",
                "PUT",
                "/users/{username}",
                "BASIC ('PROFILE_WRITE' permission)",
                "PUT /users/test",
                loadAndPrettify("/restSampleData/user_put_request.xml"),
                loadAndPrettify("/restSampleData/user_put_response.xml")));
        // Graphs ============================================================
        all.add(new RestDescription(
                "List users graphs",
                "List of users graphs. Pagination is implemented. Parameters:" +
                        "<br /> 'page' - number of page. Min=1, default=1" +
                        "<br /> 'pageSize' - size of one page. Min=1, max=50, default=50",
                "GET",
                "/users/{username}/graphs?[page=X]&[pageSize=Y]",
                "BASIC, OAUTH ('GRAPHS_READ' permission)",
                "GET /users/test/graphs?page=2&pageSize=5",
                loadAndPrettify("/restSampleData/graphs_get_response.xml")));

        all.add(new RestDescription(
                "Show graph",
                "Shows one graph of specified id",
                "GET",
                "/users/{username}/graphs/{graph_id}",
                "BASIC, OAUTH ('GRAPHS_READ' permission)",
                "GET /users/test/graphs/68",
                loadAndPrettify("/restSampleData/graph_get_response.xml")));


        all.add(new RestDescription(
                "Add graph",
                "Adds new graph. Only supported format is GEXF. Graph name can be specified using request parameter 'name'.",
                "POST",
                "/users/{username}/graphs[?name={graph_name}]",
                "BASIC, OAUTH ('GRAPHS_WRITE' permission)",
                "POST users/test/graphs?name=testGraphName",
                loadAndPrettify("/restSampleData/graphs_post_request_Misserables.gexf"),
                loadAndPrettify("/restSampleData/graphs_post_response.xml")));

        all.add(new RestDescription(
                "Show graph GEXF",
                "Shows graph in GEXF format.",
                "GET",
                "/users/{username}/graphs/{graph_id}/gexf",
                "BASIC, OAUTH ('GRAPHS_READ' permission)",
                "GET /users/test/graphs/68/gexf",
                loadAndPrettify("/restSampleData/graph_get_gexf_response.xml")));

        all.add(new RestDescription(
                "Show graph SVG",
                "Shows graph in SVG (image) format.",
                "GET",
                "/users/{username}/graphs/{graph_id}/svg",
                "BASIC, OAUTH ('GRAPHS_READ' permission)",
                "GET /users/test/graphs/68/svg",
                loadAndPrettify("/restSampleData/graph_get_svg_response.xml")));

        all.add(new RestDescription(
                "Show graph statistic report",
                "Shows graph html report of statistic function. Available only if some statistic function was applied before.",
                "GET",
                "/users/{username}/graphs/{graph_id}/statistics-report",
                "BASIC, OAUTH ('GRAPHS_READ' permission)",
                "GET /users/test/graphs/71/statistics-report",
                loadAndPrettify("/restSampleData/graph_get_stat_response.xml")));

        all.add(new RestDescription(
                "Apply function",
                "Apply layout or statistic function on graph. Creates new updated graph and returns its reference.<br/>" +
                        "General format of request xml is <function><layout/>[or]<statistic></function>",
                "PUT",
                "/users/{username}/graphs/{graph_id}",
                "BASIC, OAUTH ('GRAPHS_WRITE' permission)",
                "PUT /users/test/graphs/68",
                loadAndPrettify("/restSampleData/graph_put_request_functionClusteringCoeficient.xml"),
                loadAndPrettify("/restSampleData/graph_put_response_functionClusteringCoeficient.xml")));
        return all;
    }

    private String loadAndPrettify(String resource) {
        try {
            String xml = IOUtil.readFile(getClass().getResourceAsStream(resource));
            xml = XmlUtil.prettifyXml(xml);
            return xml;
        } catch (Exception e) {
            return "Loading error";
        }

    }
}
