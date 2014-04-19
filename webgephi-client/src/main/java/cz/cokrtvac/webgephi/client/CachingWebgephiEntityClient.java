package cz.cokrtvac.webgephi.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cz.cokrtvac.webgephi.api.model.GraphFunctionXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphsXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutsXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticsXml;
import cz.cokrtvac.webgephi.api.model.user.UserXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 15. 4. 2014
 * Time: 23:07
 */
public class CachingWebgephiEntityClient implements WebgephiEntityClient {
    private static final Logger log = LoggerFactory.getLogger(CachingWebgephiEntityClient.class);
    private static final String DELIM = "___";

    private static final String LAYOUTS_KEY = DELIM + "layouts" + DELIM;
    private static final String STATISTICS_KEY = DELIM + "statistics" + DELIM;
    private static final String LAYOUT_KEY_PREFIX = "layout" + DELIM;
    private static final String STATISTIC_KEY_PREFIX = "statistic" + DELIM;


    private WebgephiEntityClient wrapped;

    LoadingCache<String, GraphDetailXml> graphDetailXmlCache = CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .maximumSize(500)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, GraphDetailXml>() {
                        public GraphDetailXml load(String key) throws Exception {
                            String[] keys = key.split(DELIM);
                            String username = keys[0];
                            Long id = Long.valueOf(keys[1]);
                            return wrapped.getGraph(username, id);
                        }
                    }
            );

    LoadingCache<String, String> graphFormatCache = CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .maximumSize(500)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, String>() {
                        public String load(String key) throws Exception {
                            String[] keys = key.split(DELIM);
                            String username = keys[0];
                            Long id = Long.valueOf(keys[1]);
                            String format = keys[2];
                            return wrapped.getGraphInFormat(username, id, format);
                        }
                    }
            );

    LoadingCache<String, Object> functionsCache = CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .maximumSize(500)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Object>() {
                        public Object load(String key) throws Exception {
                            if (key.equals(LAYOUTS_KEY)) {
                                return wrapped.getLayouts();
                            }
                            if (key.equals(STATISTICS_KEY)) {
                                return wrapped.getStatistics();
                            }
                            if (key.startsWith(LAYOUT_KEY_PREFIX)) {
                                return wrapped.getLayout(key.substring(LAYOUT_KEY_PREFIX.length()));
                            }
                            if (key.startsWith(STATISTIC_KEY_PREFIX)) {
                                return wrapped.getStatistic(key.substring(STATISTIC_KEY_PREFIX.length()));
                            }
                            throw new IllegalArgumentException("Unknown chache format " + key);
                        }
                    }
            );

    private void rethrowError(ExecutionException e) throws ErrorHttpResponseException, WebgephiClientException, RuntimeException {
        try {
            throw e.getCause();
        } catch (ErrorHttpResponseException ex) {
            throw ex;
        } catch (WebgephiClientException ex) {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Unexected exception: " + throwable.getMessage(), throwable);
            throw new RuntimeException(throwable);
        }
    }

    public CachingWebgephiEntityClient(WebgephiEntityClient wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public UserXml getUser(String username) throws ErrorHttpResponseException, WebgephiClientException {
        return wrapped.getUser(username);
    }

    @Override
    public UserXml getLoggedUser() throws ErrorHttpResponseException, WebgephiClientException {
        return wrapped.getLoggedUser();
    }

    @Override
    public String getLogged() throws ErrorHttpResponseException, WebgephiClientException {
        return wrapped.getLogged();
    }

    @Override
    public GraphDetailXml getGraph(String username, Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        try {
            return graphDetailXmlCache.get(username + DELIM + graphId);
        } catch (ExecutionException e) {
            rethrowError(e);
            return null;
        }
    }

    @Override
    public GraphDetailXml getGraph(Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraph(getLogged(), graphId);
    }

    @Override
    public GraphsXml getGraphs(String username, long page, int pageSize, boolean desc) throws ErrorHttpResponseException, WebgephiClientException {
        return wrapped.getGraphs(username, page, pageSize, desc);
    }

    @Override
    public GraphsXml getGraphs(long page, int pageSize, boolean desc) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphs(getLogged(), page, pageSize, desc);
    }

    @Override
    public GraphDetailXml addGraph(String username, String graphName, Document graphGexf) throws ErrorHttpResponseException, WebgephiClientException {
        return wrapped.addGraph(username, graphName, graphGexf);
    }

    @Override
    public GraphDetailXml addGraph(String graphName, Document graphGexf) throws ErrorHttpResponseException, WebgephiClientException {
        return addGraph(getLogged(), graphName, graphGexf);
    }

    @Override
    public GraphDetailXml applyFunction(String username, Long graphId, GraphFunctionXml function, String newName) throws ErrorHttpResponseException, WebgephiClientException {
        return wrapped.applyFunction(username, graphId, function, newName);
    }

    @Override
    public GraphDetailXml applyFunction(Long graphId, GraphFunctionXml function, String newName) throws ErrorHttpResponseException, WebgephiClientException {
        return wrapped.applyFunction(graphId, function, newName);
    }

    @Override
    public GraphDetailXml applyLayoutFunction(String username, Long graphId, LayoutXml layoutXml, String newName, int repeat) throws ErrorHttpResponseException, WebgephiClientException {
        return wrapped.applyLayoutFunction(username, graphId, layoutXml, newName, repeat);
    }

    @Override
    public GraphDetailXml applyLayoutFunction(Long graphId, LayoutXml layoutXml, String newName, int repeat) throws ErrorHttpResponseException, WebgephiClientException {
        return wrapped.applyLayoutFunction(graphId, layoutXml, newName, repeat);
    }

    @Override
    public String getGraphInFormat(String username, Long graphId, String format) throws ErrorHttpResponseException, WebgephiClientException {
        try {
            return graphFormatCache.get(username + DELIM + graphId + DELIM + format);
        } catch (ExecutionException e) {
            rethrowError(e);
            return null;
        }
    }

    @Override
    public String getGraphInFormat(Long graphId, String format) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphInFormat(getLogged(), graphId, format);
    }

    @Override
    public String getGraphAsSvg(String username, Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphInFormat(username, graphId, SVG);
    }

    @Override
    public String getGraphAsSvg(Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphAsSvg(getLogged(), graphId);
    }

    @Override
    public String getGraphAsGexf(String username, Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphInFormat(username, graphId, GEXF);
    }

    @Override
    public String getGraphAsGexf(Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphAsGexf(getLogged(), graphId);
    }

    @Override
    public String getGraphStatisticReport(String username, Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphInFormat(username, graphId, STATISTICS_REPORT);
    }

    @Override
    public String getGraphStatisticReport(Long graphId) throws ErrorHttpResponseException, WebgephiClientException {
        return getGraphStatisticReport(getLogged(), graphId);
    }

    @Override
    public LayoutsXml getLayouts() throws ErrorHttpResponseException, WebgephiClientException {
        try {
            return (LayoutsXml) functionsCache.get(LAYOUTS_KEY);
        } catch (ExecutionException e) {
            rethrowError(e);
            return null;
        }
    }

    @Override
    public LayoutXml getLayout(String layoutId) throws ErrorHttpResponseException, WebgephiClientException {
        try {
            return (LayoutXml) functionsCache.get(LAYOUT_KEY_PREFIX + layoutId);
        } catch (ExecutionException e) {
            rethrowError(e);
            return null;
        }
    }

    @Override
    public StatisticsXml getStatistics() throws ErrorHttpResponseException, WebgephiClientException {
        try {
            return (StatisticsXml) functionsCache.get(STATISTICS_KEY);
        } catch (ExecutionException e) {
            rethrowError(e);
            return null;
        }
    }

    @Override
    public StatisticXml getStatistic(String statisticId) throws ErrorHttpResponseException, WebgephiClientException {
        try {
            return (StatisticXml) functionsCache.get(STATISTIC_KEY_PREFIX + statisticId);
        } catch (ExecutionException e) {
            rethrowError(e);
            return null;
        }
    }

    // -----------------------------------------------------------
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
