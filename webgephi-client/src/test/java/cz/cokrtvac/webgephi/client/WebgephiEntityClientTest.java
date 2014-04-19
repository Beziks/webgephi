package cz.cokrtvac.webgephi.client;

import cz.cokrtvac.webgephi.api.model.GraphFunctionXml;
import cz.cokrtvac.webgephi.api.model.PropertyXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphsXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutsXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticsXml;
import cz.cokrtvac.webgephi.api.model.user.UserXml;
import cz.cokrtvac.webgephi.api.util.XmlFastUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 8.2.14
 * Time: 20:13
 */
public class WebgephiEntityClientTest {
    private Logger log = LoggerFactory.getLogger(getClass());

    private WebgephiEntityClient client;
    private Long firstGraphId;

    @BeforeClass
    public void init() throws WebgephiClientException {
        // Access token to testuser account (all scopes)
        client = new WebgephiEntityClientImpl(new WebgephiOAuthClient("https://webgephi.local:8443/rest", TestsUtil.getTestAccessToken()));
        firstGraphId = client.get("users/testuser/graphs").readEntity(GraphsXml.class).getGraphs().get(0).getId();
    }

    @BeforeMethod
    public void setUp() throws Exception {
    }

    @AfterMethod
    public void tearDown() throws Exception {
    }

    @Test
    public void testUser() throws Exception {
        UserXml user = client.getUser("testuser");
        log.info("Response: " + user);

        GraphDetailXml graph = client.getGraph("testuser", firstGraphId);
        log.info("Response: " + graph);

        String svg = client.getGraphAsSvg("testuser", firstGraphId);
        log.info("Response: " + svg);

        String gexf = client.getGraphAsGexf("testuser", firstGraphId);
        log.info("Response: " + gexf);
    }

    @Test
    public void testLoggedUser() throws Exception {
        UserXml user = client.getLoggedUser();
        Assert.assertEquals(user.getUsername(), "testuser");
        log.info("Response: " + user);
    }

    @Test
    public void testGraphs() throws Exception {
        GraphsXml graphs = client.getGraphs(1, 2, false);
        log.info("Response: " + graphs);

        try {
            GraphsXml graphs2 = client.getGraphs("user", 1, 2, false);
            log.info("Response: " + graphs2);
        } catch (ErrorHttpResponseException e) {
            log.info(e.getMessage());
            Assert.assertTrue(true);
            return;
        }
        Assert.assertTrue(false);
    }

    @Test
    public void testGraphsDesc() throws Exception {
        GraphsXml graphsAsc = client.getGraphs(1, 2, false);
        Assert.assertTrue(graphsAsc.getGraphs().get(0).getId() < graphsAsc.getGraphs().get(1).getId());

        GraphsXml graphsDesc = client.getGraphs(1, 2, true);
        Assert.assertTrue(graphsDesc.getGraphs().get(0).getId() > graphsDesc.getGraphs().get(1).getId());
    }

    @Test
    public void testGetFunctions() throws Exception {
        LayoutsXml layoutsXml = client.getLayouts();
        log.info("Response: " + layoutsXml);

        LayoutXml layoutXml = client.getLayout("forceatlas-2");
        log.info("Response: " + layoutXml);

        StatisticsXml statisticsXml = client.getStatistics();
        log.info("Response: " + statisticsXml);

        StatisticXml statisticXml = client.getStatistic("page-rank");
        log.info("Response: " + statisticsXml);
    }

    @Test
    public void testModifyGraph() throws Exception {
        // Create graph
        GraphDetailXml g = client.addGraph("testGraph", XmlFastUtil.lsDeSerializeDom(TestsUtil.getMisserablesGEXF()));
        Assert.assertEquals(g.getName(), "testGraph");
        log.info("Response: " + g);

        // Apply layout
        LayoutXml layout = client.getLayout("clockwise-rotate");

        PropertyXml<Double> angle = (PropertyXml<Double>) layout.getProperty("clockwise.angle.name");
        angle.setValue(3.14);

        GraphFunctionXml f = new GraphFunctionXml();
        f.setFunction(layout);

        GraphDetailXml g2 = client.applyFunction(g.getId(), f, "afterLayout");

        log.info("Response after layout: " + g2);
        Assert.assertNotEquals(g2.getId(), g.getId());
        Assert.assertEquals(g2.getName(), "afterLayout");
        Assert.assertFalse(g2.hasStatistics());

        try {
            client.getGraphStatisticReport(g2.getId());
            Assert.assertTrue(false);
        } catch (ErrorHttpResponseException e) {
            log.info(e.getMessage());
        }

        // Apply statistics
        StatisticXml stat = client.getStatistic("page-rank");
        GraphFunctionXml f2 = new GraphFunctionXml();
        f2.setFunction(stat);

        GraphDetailXml g3 = client.applyFunction(g.getId(), f2, "afterStat");

        log.info("Response after statistic: " + g3);
        Assert.assertNotEquals(g3.getId(), g2.getId());
        Assert.assertEquals(g3.getName(), "afterStat");
        Assert.assertTrue(g3.hasStatistics());

        // get report
        String html = client.getGraphStatisticReport(g3.getId());
        Assert.assertTrue(html.contains("PageRank Report"));
        log.info("Statistic report: " + html);
    }

    @Test(expectedExceptions = ErrorHttpResponseException.class)
    public void testException() throws Exception {
        try {
            String stat = client.getGraphStatisticReport("testuser", firstGraphId);
            log.info("Response: " + stat);
        } catch (ErrorHttpResponseException e) {
            log.info("Response: " + e.getMessage());
            throw e;
        }
    }


}
