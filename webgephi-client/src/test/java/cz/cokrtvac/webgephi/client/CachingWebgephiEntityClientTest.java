package cz.cokrtvac.webgephi.client;

import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutsXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticsXml;
import cz.cokrtvac.webgephi.api.model.user.UserXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 8.2.14
 * Time: 20:13
 */
public class CachingWebgephiEntityClientTest {
    private Logger log = LoggerFactory.getLogger(getClass());

    private Token token;
    private WebgephiEntityClient client;
    private Long firstGraphId;

    @BeforeClass
    public void init() throws WebgephiClientException, ErrorHttpResponseException {
        // Access token to testuser account (all scopes)

        firstGraphId = client.getGraphs(1, 1, false).getGraphs().get(0).getId();
    }

    @Test
    public void testGraphDetailXml() throws Exception {
        UserXml user = client.getUser("testuser");
        log.info("Response: " + user);

        GraphDetailXml graph = client.getGraph(firstGraphId);
        log.info("Response: " + graph);

        GraphDetailXml graphFromCache = client.getGraph(firstGraphId);
        log.info("Response: " + graph);

        Assert.assertTrue(graph == graphFromCache);
    }

    @Test
    public void testGraphFormat() throws Exception {
        String svg = client.getGraphAsSvg(firstGraphId);
        String svgFromCache = client.getGraphAsSvg(firstGraphId);
        String svgFromCache2 = client.getGraphAsSvg(firstGraphId);
        log.info("Response: " + svg);

        Assert.assertTrue(svg == svgFromCache && svg == svgFromCache2);

        String gexf = client.getGraphAsGexf(firstGraphId);
        String gexfFromCache = client.getGraphAsGexf(firstGraphId);
        String gexfFromCache2 = client.getGraphAsGexf(firstGraphId);
        log.info("Response: " + gexf);

        Assert.assertTrue(gexf == gexfFromCache && gexf == gexfFromCache2);
    }

    @Test(expectedExceptions = ErrorHttpResponseException.class)
    public void testError() throws WebgephiClientException, ErrorHttpResponseException {
        client.getGraph(-5l);
    }

    @Test
    public void testFunctionsList() throws Exception {
        log.info("First layout: " + client.getLayout(client.getLayouts().getFunctions().get(0).getId()));
        log.info("First stat: " + client.getStatistic(client.getStatistics().getFunctions().get(0).getId()));

        LayoutsXml layoutsXml = client.getLayouts();
        StatisticsXml statisticsXml = client.getStatistics();

        Assert.assertTrue(layoutsXml == client.getLayouts());
        Assert.assertTrue(statisticsXml == client.getStatistics());

        String layoutId = layoutsXml.getFunctions().get(1).getId();
        Assert.assertTrue(client.getLayout(layoutId) == client.getLayout(layoutId));

        String statId = statisticsXml.getFunctions().get(1).getId();
        Assert.assertTrue(client.getStatistic(statId) == client.getStatistic(statId));


    }


}
