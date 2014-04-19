package cz.cokrtvac.webgephi.clientapp.model.data_source;

import com.vaadin.ui.Notification;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphsXml;
import cz.cokrtvac.webgephi.client.ErrorHttpResponseException;
import cz.cokrtvac.webgephi.client.WebgephiClientException;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 6. 4. 2014
 * Time: 12:48
 */
public class GraphsQuery extends AbstractBeanQuery<GraphDetailXml> {
    private static Logger log = LoggerFactory.getLogger(GraphsQuery.class);
    private QueryDefinition definition;
    private UserSession userSession;
    private int pageSize = 40;

    public GraphsQuery(QueryDefinition queryDefinition, Map<String, Object> queryConfiguration, Object[] sortPropertyIds, boolean[] sortStates) {
        super(queryDefinition, queryConfiguration, sortPropertyIds, sortStates);
        this.definition = queryDefinition;
        this.userSession = (UserSession) queryConfiguration.get("userSession");
    }

    @Override
    protected GraphDetailXml constructBean() {
        return new GraphDetailXml();
    }

    @Override
    public int size() {
        try {
            GraphsXml graphsXml = userSession.getWebgephiClient().getGraphs(1, 1, true);
            String href = graphsXml.getLast().getHref();
            int start = href.indexOf("page=") + 5;
            int end = href.indexOf("&", start);
            if(end < 0){
                end = href.length();
            }

            String size = href.substring(start, end);
            return Integer.valueOf(size);
        } catch (ErrorHttpResponseException e) {
            log.error("Cannot fetch data from server", e);
        } catch (WebgephiClientException e) {
            log.error("Cannot fetch data from server", e);
        }


        return 1;
    }


    @Override
    protected List<GraphDetailXml> loadBeans(int startIndex, int count) {
        int page = startIndex / pageSize;
        page = page + 1;

        int offset = (page - 1) * pageSize;

        List<GraphDetailXml> graphs = new ArrayList<GraphDetailXml>();
        int firstGraph =  startIndex - offset;

        while(graphs.size() < count + firstGraph){
            loadPage(page, graphs);
            page++;
        }

        Notification.show("Loaded graph data", "Graph rows " + startIndex + " -> " + (startIndex + count), Notification.Type.TRAY_NOTIFICATION);
        return graphs.subList(firstGraph, firstGraph + count);
    }

    private void loadPage(int pageNumber, List<GraphDetailXml> graphs){
        try {
            GraphsXml graphsXml = userSession.getWebgephiClient().getGraphs(pageNumber, pageSize, true);
            graphs.addAll(graphsXml.getGraphs());
        } catch (ErrorHttpResponseException e) {
            log.error("Cannot fetch data from server", e);
            throw new RuntimeException(e);
        } catch (WebgephiClientException e) {
            log.error("Cannot fetch data from server", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void saveBeans(List<GraphDetailXml> graphDetailXmls, List<GraphDetailXml> graphDetailXmls2, List<GraphDetailXml> graphDetailXmls3) {
        throw new UnsupportedOperationException();
    }
}

