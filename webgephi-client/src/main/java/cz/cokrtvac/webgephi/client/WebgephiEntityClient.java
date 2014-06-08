package cz.cokrtvac.webgephi.client;

import cz.cokrtvac.webgephi.api.model.GraphFunctionXml;
import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.api.model.filter.FiltersXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphsXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutsXml;
import cz.cokrtvac.webgephi.api.model.ranking.RankingXml;
import cz.cokrtvac.webgephi.api.model.ranking.RankingsXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticsXml;
import cz.cokrtvac.webgephi.api.model.user.UserXml;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 15. 4. 2014
 * Time: 23:06
 */
public interface WebgephiEntityClient extends WebgephiClient {
    public static final String SVG = "svg";
    public static final String GEXF = "gexf";
    public static final String STATISTICS_REPORT = "statistics-report";

    // User ------------------------------------
    UserXml getUser(String username) throws ErrorHttpResponseException, WebgephiClientException;

    UserXml getLoggedUser() throws ErrorHttpResponseException, WebgephiClientException;

    String getLogged() throws ErrorHttpResponseException, WebgephiClientException;

    // GET Graph ------------------------------------
    GraphDetailXml getGraph(String username, Long graphId) throws ErrorHttpResponseException, WebgephiClientException;

    GraphDetailXml getGraph(Long graphId) throws ErrorHttpResponseException, WebgephiClientException;

    GraphsXml getGraphs(String username, long page, int pageSize, boolean desc) throws ErrorHttpResponseException, WebgephiClientException;

    GraphsXml getGraphs(long page, int pageSize, boolean desc) throws ErrorHttpResponseException, WebgephiClientException;

    // POST Graph ------------------------------------

    /**
     * @param username
     * @param graphName
     * @param format    - Format of input data. If null, content will be resolved from content (works for gexf, dl, gdf, gml, graphml, net, vna, dot, tlp, csv). All formats supported by Gephi are supported.
     * @param content   - Graph content. Supported: gexf, dl, gdf, gml, graphml, net, vna, dot, tlp, csv.
     * @return
     * @throws ErrorHttpResponseException
     * @throws WebgephiClientException
     */
    GraphDetailXml addGraph(String username, String graphName, String format, String content) throws ErrorHttpResponseException, WebgephiClientException;

    GraphDetailXml addGraph(String graphName, String format, String content) throws ErrorHttpResponseException, WebgephiClientException;

    // APPLY function ------------------------------------
    GraphDetailXml applyFunction(String username, Long graphId, GraphFunctionXml function, String newName) throws ErrorHttpResponseException, WebgephiClientException;

    GraphDetailXml applyFunction(Long graphId, GraphFunctionXml function, String newName) throws ErrorHttpResponseException, WebgephiClientException;

    GraphDetailXml applyLayoutFunction(String username, Long graphId, LayoutXml layoutXml, String newName, int repeat) throws ErrorHttpResponseException, WebgephiClientException;

    GraphDetailXml applyLayoutFunction(Long graphId, LayoutXml layoutXml, String newName, int repeat) throws ErrorHttpResponseException, WebgephiClientException;

    GraphDetailXml applyStatisticFunction(String username, Long graphId, StatisticXml statisticXml, String newName) throws ErrorHttpResponseException, WebgephiClientException;

    GraphDetailXml applyStatisticFunction(Long graphId, StatisticXml statisticXml, String newName) throws ErrorHttpResponseException, WebgephiClientException;

    GraphDetailXml applyRankingFunction(String username, Long graphId, RankingXml rankingXml, String newName) throws ErrorHttpResponseException, WebgephiClientException;

    GraphDetailXml applyRankingFunction(Long graphId, RankingXml rankingXml, String newName) throws ErrorHttpResponseException, WebgephiClientException;

    public GraphDetailXml applyFilterFunction(String username, Long graphId, FilterXml filterXml, String newName) throws ErrorHttpResponseException, WebgephiClientException;

    public GraphDetailXml applyFilterFunction(Long graphId, FilterXml filterXml, String newName) throws ErrorHttpResponseException, WebgephiClientException;

    // Graph formats ------------------------------------
    String getGraphInFormat(String username, Long graphId, String format) throws ErrorHttpResponseException, WebgephiClientException;

    String getGraphInFormat(Long graphId, String format) throws ErrorHttpResponseException, WebgephiClientException;

    // SVG ------------------------------------
    String getGraphAsSvg(String username, Long graphId) throws ErrorHttpResponseException, WebgephiClientException;

    String getGraphAsSvg(Long graphId) throws ErrorHttpResponseException, WebgephiClientException;

    // GEXF ------------------------------------
    String getGraphAsGexf(String username, Long graphId) throws ErrorHttpResponseException, WebgephiClientException;

    String getGraphAsGexf(Long graphId) throws ErrorHttpResponseException, WebgephiClientException;

    // STATISTICS ------------------------------------
    String getGraphStatisticReport(String username, Long graphId) throws ErrorHttpResponseException, WebgephiClientException;

    String getGraphStatisticReport(Long graphId) throws ErrorHttpResponseException, WebgephiClientException;

    // FUNCTIONS ------------------------------------
    LayoutsXml getLayouts() throws ErrorHttpResponseException, WebgephiClientException;

    LayoutXml getLayout(String layoutId) throws ErrorHttpResponseException, WebgephiClientException;

    StatisticsXml getStatistics() throws ErrorHttpResponseException, WebgephiClientException;

    StatisticXml getStatistic(String statisticId) throws ErrorHttpResponseException, WebgephiClientException;

    RankingsXml getRankings() throws ErrorHttpResponseException, WebgephiClientException;

    RankingXml getRanking(String rankingId) throws ErrorHttpResponseException, WebgephiClientException;

    FiltersXml getFilters() throws ErrorHttpResponseException, WebgephiClientException;

    FilterXml getFilter(String filterId) throws ErrorHttpResponseException, WebgephiClientException;

    // Wrapped methods ======================================================================
    @Override
    Response get(String targetPath) throws WebgephiClientException;

    @Override
    Response put(String targetPath, MediaType bodyMediaType, Object body) throws WebgephiClientException;

    @Override
    Response post(String targetPath, MediaType bodyMediaType, Object body) throws WebgephiClientException;
}
