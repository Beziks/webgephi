package cz.cokrtvac.webgephi.client;

import cz.cokrtvac.webgephi.api.model.graph.GraphsXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 8.2.14
 * Time: 20:13
 */
public class WebgephiClientTest {
    private Logger log = LoggerFactory.getLogger(getClass());

    private WebgephiOAuthClient client;
    private Long firstGraphId;

    @BeforeClass
    public void init() throws WebgephiClientException {
        // Access token to testuser account (all scopes)
        client = new WebgephiOAuthClient("https://webgephi.local:8443/rest", TestsUtil.getTestAccessToken());
        firstGraphId = client.get("users/testuser/graphs").readEntity(GraphsXml.class).getGraphs().get(0).getId();
    }

    @BeforeMethod
    public void setUp() throws Exception {

    }

    @AfterMethod
    public void tearDown() throws Exception {
    }

    @Test
    public void testGet() throws Exception {
        testUrl("users", 403, "<error");
        testUrl("users/testuser", 200, "<user");
        testUrl("users/testuser/graphs", 200, "Missereables");
        testUrl("users/user", 403, "<error");
        testUrl("users/user/graphs", 403, "<error");
        testUrl("layouts", 200, "<layout");
        testUrl("statistics", 200, "<statistic");
    }

    @Test
    public void testPost() throws Exception {
        String body = TestsUtil.getMisserablesGEXF();
        Response r = client.post("users/testuser/graphs", null, body);
        log.info(r.getStatus() + " | " + r.getEntity());
        Assert.assertEquals(r.getStatus(), 201);
    }

    //@Test
    public void testLargePost() throws Exception {
        String body = TestsUtil.getTwitterGEXF();
        Response r = client.post("users/testuser/graphs", null, body);
        log.info(r.getStatus() + " | " + r.getEntity());
        Assert.assertEquals(r.getStatus(), 201);
    }

    @Test
    public void testPutLayout() throws Exception {
        String body = TestsUtil.getLayoutRotate();
        Response r = client.put("users/testuser/graphs/" + firstGraphId, null, body);
        log.info(r.getStatus() + " | " + r.getEntity());
        Assert.assertEquals(r.getStatus(), 201);
    }

    @Test
    public void testPutStatistic() throws Exception {
        String body = TestsUtil.getStatisticClusteringCoeficient();
        Response r = client.put("users/testuser/graphs/" + firstGraphId, null, body);
        log.info(r.getStatus() + " | " + r.getEntity());
        Assert.assertEquals(r.getStatus(), 201);
    }

    private void testUrl(String resource, int expectedStatus, String contains) throws WebgephiClientException {
        try {
            Response r = client.get(resource);
            log.info(r.getStatus() + " | " + r.getEntity());
            Assert.assertEquals(r.getStatus(), expectedStatus);
            Assert.assertTrue(r.readEntity(String.class).contains(contains));
        } catch (WebgephiClientException e) {
            log.error("Test failed: " + e.getMessage(), e);
            throw e;
        }
    }
}
