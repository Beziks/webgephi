package cz.cokrtvac.webgephi.client;

import cz.cokrtvac.webgephi.api.model.GraphFunctionXml;
import cz.cokrtvac.webgephi.api.model.error.Code;
import cz.cokrtvac.webgephi.api.model.error.ErrorXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphsXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutsXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticsXml;
import cz.cokrtvac.webgephi.api.model.user.UserXml;
import cz.cokrtvac.webgephi.api.util.XmlFastUtil;
import cz.cokrtvac.webgephi.client.util.UrlUtil;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 23. 3. 2014
 * Time: 17:51
 */
public class WebgephiEntityClientImpl implements WebgephiEntityClient {
    private WebgephiClient wrapped;
    private String loggedUser;

    public WebgephiEntityClientImpl(WebgephiClient baseClient) {
        this.wrapped = baseClient;
    }

    public String getLogged() throws ErrorHttpResponseException, WebgephiClientException {
        if (loggedUser == null) {
            loggedUser = getLoggedUser().getUsername();
        }
        return loggedUser;
    }

    // User ------------------------------------
    @Override
    public UserXml getUser(String username) throws ErrorHttpResponseException, WebgephiClientException {
        UserXml res = get("users/" + username, UserXml.class);
        return res;
    }

    @Override
    public UserXml getLoggedUser() throws ErrorHttpResponseException, WebgephiClientException {
        UserXml res = get("users/logged", UserXml.class);
        return res;
    }

    // GET Graph ------------------------------------
    @Override
    public GraphDetailXml getGraph(String username, Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        GraphDetailXml res = get("users/" + username + "/graphs/" + graphId, GraphDetailXml.class);
        return res;
    }

    @Override
    public GraphDetailXml getGraph(Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraph(getLogged(), graphId);
    }

    @Override
    public GraphsXml getGraphs(String username, long page, int pageSize, boolean desc) throws ErrorHttpResponseException, WebgephiClientException {
        String descString = desc ? "&desc=true" : "";
        return get("users/" + username + "/graphs?page=" + page + "&pageSize=" + pageSize + descString, GraphsXml.class);
    }

    @Override
    public GraphsXml getGraphs(long page, int pageSize, boolean desc) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphs(getLogged(), page, pageSize, desc);
    }

    // POST Graph ------------------------------------
    @Override
    public GraphDetailXml addGraph(String username, String graphName, Document graphGexf) throws ErrorHttpResponseException, WebgephiClientException {
        return post("users/" + username + "/graphs?name=" + UrlUtil.encode(graphName), GraphDetailXml.class, MediaType.APPLICATION_XML_TYPE, graphGexf);
    }

    @Override
    public GraphDetailXml addGraph(String graphName, Document graphGexf) throws ErrorHttpResponseException, WebgephiClientException {
        return addGraph(getLogged(), graphName, graphGexf);
    }

    // APPLY function ------------------------------------
    @Override
    public GraphDetailXml applyFunction(String username, Long graphId, GraphFunctionXml function, String newName) throws ErrorHttpResponseException, WebgephiClientException {
        String url = "users/" + username + "/graphs/" + graphId;
        if (newName != null) {
            url += "?name=" + UrlUtil.encode(newName);
        }
        return put(url, GraphDetailXml.class, MediaType.APPLICATION_XML_TYPE, function);
    }

    @Override
    public GraphDetailXml applyFunction(Long graphId, GraphFunctionXml function, String newName) throws ErrorHttpResponseException, WebgephiClientException {
        return applyFunction(getLogged(), graphId, function, newName);
    }

    @Override
    public GraphDetailXml applyLayoutFunction(String username, Long graphId, LayoutXml layoutXml, String newName, int repeat) throws ErrorHttpResponseException, WebgephiClientException {
        String url = "users/" + username + "/graphs/" + graphId;
        url += "?repeat=" + repeat;
        if (newName != null) {
            url += "&name=" + UrlUtil.encode(newName);
        }
        return put(url, GraphDetailXml.class, MediaType.APPLICATION_XML_TYPE, new GraphFunctionXml(layoutXml));
    }

    @Override
    public GraphDetailXml applyLayoutFunction(Long graphId, LayoutXml layoutXml, String newName, int repeat) throws ErrorHttpResponseException, WebgephiClientException {
        return applyLayoutFunction(getLogged(), graphId, layoutXml, newName, repeat);
    }

    // Graph formats ------------------------------------
    @Override
    public String getGraphInFormat(String username, Long graphId, String format) throws ErrorHttpResponseException, WebgephiClientException {
        String res = get("users/" + username + "/graphs/" + graphId + "/" + format, String.class);
        return res;
    }

    @Override
    public String getGraphInFormat(Long graphId, String format) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphInFormat(getLogged(), graphId, format);
    }

    // SVG ------------------------------------
    @Override
    public String getGraphAsSvg(String username, Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphInFormat(username, graphId, SVG);
    }

    @Override
    public String getGraphAsSvg(Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphAsSvg(getLogged(), graphId);
    }

    // GEXF ------------------------------------
    @Override
    public String getGraphAsGexf(String username, Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphInFormat(username, graphId, GEXF);
    }

    @Override
    public String getGraphAsGexf(Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphAsGexf(getLogged(), graphId);
    }

    // STATISTICS ------------------------------------
    @Override
    public String getGraphStatisticReport(String username, Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphInFormat(username, graphId, STATISTICS_REPORT);
    }

    @Override
    public String getGraphStatisticReport(Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphInFormat(getLogged(), graphId, "statistics-report");
    }

    // FUNCTIONS ------------------------------------
    @Override
    public LayoutsXml getLayouts() throws ErrorHttpResponseException, WebgephiClientException {
        return get("layouts", LayoutsXml.class);
    }

    @Override
    public LayoutXml getLayout(String layoutId) throws ErrorHttpResponseException, WebgephiClientException {
        return get("layouts/" + layoutId, LayoutXml.class);
    }

    @Override
    public StatisticsXml getStatistics() throws ErrorHttpResponseException, WebgephiClientException {
        return get("statistics", StatisticsXml.class);
    }

    @Override
    public StatisticXml getStatistic(String statisticId) throws ErrorHttpResponseException, WebgephiClientException {
        return get("statistics/" + statisticId, StatisticXml.class);
    }

    // Utils ==============================================================================
    protected <T> T get(String targetPath, Class<T> responseType) throws ErrorHttpResponseException, WebgephiClientException {
        Response response = get(targetPath);
        try {
            if (response.getStatus() < 200 || response.getStatus() > 299) {
                ErrorXml err = parseError(response);
                throw new ErrorHttpResponseException(err);
            }
            T res = response.readEntity(responseType);
            return res;
        } finally {
            response.close();
        }
    }

    protected <T> T post(String targetPath, Class<T> responseType, MediaType bodyMediaType, Object body) throws ErrorHttpResponseException, WebgephiClientException {
        Response response = post(targetPath, bodyMediaType, body);
        try {
            if (response.getStatus() < 200 || response.getStatus() > 299) {
                ErrorXml err = parseError(response);
                throw new ErrorHttpResponseException(err);
            }
            T res = response.readEntity(responseType);
            return res;
        } finally {
            response.close();
        }
    }

    protected <T> T put(String targetPath, Class<T> responseType, MediaType bodyMediaType, Object body) throws ErrorHttpResponseException, WebgephiClientException {
        Response response = put(targetPath, bodyMediaType, body);
        try {
            if (response.getStatus() < 200 || response.getStatus() > 299) {
                ErrorXml err = parseError(response);
                throw new ErrorHttpResponseException(err);
            }
            T res = response.readEntity(responseType);
            return res;
        } finally {
            response.close();
        }
    }

    private ErrorXml parseError(Response response) {
        if (MediaType.APPLICATION_XML_TYPE.isCompatible(response.getMediaType())) {
            ErrorXml err = response.readEntity(ErrorXml.class);
            return err;
        }

        String s = response.readEntity(String.class);

        // Parse HTML error message
        if (MediaType.TEXT_HTML_TYPE.isCompatible(response.getMediaType()) && s.contains("webgephiErrorReport")) {
            try {
                Document doc = XmlFastUtil.lsDeSerializeDom(s);
                Node message = XPathAPI.selectSingleNode(doc, "/html/body/ul/li/span[@class='webgephiErrorMessage']");
                Node detail = XPathAPI.selectSingleNode(doc, "/html/body/ul/li/span[@class='webgephiErrorDetail']");

                if (message != null) {
                    String messageText = message.getTextContent();
                    String detailText = null;
                    if (detail != null) {
                        detailText = detail.getTextContent();
                    }
                    Code c = new Code(response.getStatus(), response.getStatusInfo().getReasonPhrase());
                    return new ErrorXml(c, messageText, detailText);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Code code = new Code(response.getStatus(), response.getStatusInfo().getReasonPhrase());
        ErrorXml err = new ErrorXml(code, s);
        return err;
    }

    // Wrapped methods ======================================================================
    @Override
    public Response get(String targetPath) throws WebgephiClientException {
        return wrapped.get(targetPath);
    }

    @Override
    public Response put(String targetPath, MediaType bodyMediaType, Object body) throws WebgephiClientException {
        return wrapped.put(targetPath, bodyMediaType, body);
    }

    @Override
    public Response post(String targetPath, MediaType bodyMediaType, Object body) throws WebgephiClientException {
        return wrapped.post(targetPath, bodyMediaType, body);
    }
}
