package cz.cokrtvac.webgephi.client;

import org.jboss.resteasy.client.ClientResponse;
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
public class WebgephiClientTest {
    private Logger log = LoggerFactory.getLogger(getClass());

    private Token token;
    private WebgephiClient client;

    @BeforeClass
    public void init() {
        // Access token to admin account (all scopes)
        token = new Token("client.webgephi.cz", "68c17d0d-5090-4b0e-bb2f-f4fe50d83704", "060a7ba5-d8c0-4969-a832-714b05f81440", "b51b92a5-c65c-492f-8ad7-e9dcb459ab2e");
        client = new WebgephiClient("https://127.0.0.1:8443/webgephiserver/rest", token);
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
        testUrl("users/admin", 200, "<user");
        testUrl("users/admin/graphs", 200, "Missereables");
        testUrl("users/user", 403, "<error");
        testUrl("users/user/graphs", 403, "<error");
        testUrl("layouts", 200, "<layout");
        testUrl("statistics", 200, "<statistic");
    }

    @Test
    public void testPost() throws Exception {
        String body = TestsUtil.getMisserablesGEXF();
        ClientResponse<String> r = client.post("users/admin/graphs", String.class, null, body);
        log.info(r.getStatus() + " | " + r.getEntity());
        Assert.assertEquals(r.getStatus(), 201);
    }

    //@Test
    public void testLargePost() throws Exception {
        String body = TestsUtil.getTwitterGEXF();
        ClientResponse<String> r = client.post("users/admin/graphs", String.class, null, body);
        log.info(r.getStatus() + " | " + r.getEntity());
        Assert.assertEquals(r.getStatus(), 201);
    }

    @Test
    public void testPutLayout() throws Exception {
        String body = TestsUtil.getLayoutRotate();
        ClientResponse<String> r = client.put("users/admin/graphs/18", String.class, null, body);
        log.info(r.getStatus() + " | " + r.getEntity());
        Assert.assertEquals(r.getStatus(), 201);
    }

    @Test
    public void testPutStatistic() throws Exception {
        String body = TestsUtil.getStatisticClusteringCoeficient();
        ClientResponse<String> r = client.put("users/admin/graphs/18", String.class, null, body);
        log.info(r.getStatus() + " | " + r.getEntity());
        Assert.assertEquals(r.getStatus(), 201);
    }

    private void testUrl(String resource, int expectedStatus, String contains) throws WebgephiClientException {
        ClientResponse<String> r = client.get(resource, String.class);
        log.info(r.getStatus() + " | " + r.getEntity());
        Assert.assertEquals(r.getStatus(), expectedStatus);
        Assert.assertTrue(r.getEntity().contains(contains));
    }
}
