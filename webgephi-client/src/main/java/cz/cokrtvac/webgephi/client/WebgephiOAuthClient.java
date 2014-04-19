package cz.cokrtvac.webgephi.client;

import cz.cokrtvac.webgephi.client.util.ClientRequestFactory;
import cz.cokrtvac.webgephi.client.util.UrlUtil;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Map;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 28.1.14
 * Time: 22:56
 */
public class WebgephiOAuthClient implements WebgephiClient {
    private Logger log = LoggerFactory.getLogger(WebgephiOAuthClient.class);

    private Token accessToken;
    private String resourceBase;

    /**
     * @param resourceBase - Base address of REST servises on server. E.g. https://webgephi.cz/rest
     * @param accessToken  - Access token obtained from user. If null, only public resources are available (functions)
     */
    public WebgephiOAuthClient(String resourceBase, Token accessToken) {
        this.resourceBase = resourceBase;
        this.accessToken = accessToken;
    }

    /**
     * Send authorized GET request to defined url
     *
     * @param targetPath
     * @return
     * @throws Exception
     */
    @Override
    public Response get(String targetPath) throws WebgephiClientException {
        return request("GET", targetPath, null, null);
    }

    /**
     * Send authorized PUT request to defined url
     *
     * @param targetPath
     * @param bodyMediaType - mediaType of body content, default is application/xml (if null)
     * @param body          - body content of http request
     * @return
     * @throws WebgephiClientException
     */
    @Override
    public Response put(String targetPath, MediaType bodyMediaType, Object body) throws WebgephiClientException {
        return request("PUT", targetPath, bodyMediaType, body);
    }

    /**
     * Send authorized POST request to defined url
     *
     * @param targetPath
     * @param bodyMediaType - mediaType of body content, default is 'MediaType.WILDCARD' (if null)
     * @param body          - body content of http request
     * @return
     * @throws WebgephiClientException
     */
    @Override
    public Response post(String targetPath, MediaType bodyMediaType, Object body) throws WebgephiClientException {
        return request("POST", targetPath, bodyMediaType, body);
    }

    private Response request(String method, String targetPath, MediaType bodyMediaType, Object body) throws WebgephiClientException {
        String resource = UrlUtil.concat(resourceBase, targetPath);
        log.debug("Requesting resource on url: " + method + " " + resource);

        try {
            String url = getEndUserURL(method, resource, accessToken.getConsumerKey(), accessToken.getConsumerSecret(), accessToken.getToken(), accessToken.getSecret());

            Client client = ClientRequestFactory.create();

            WebTarget target = client.target(url);

            if(bodyMediaType == null){
                bodyMediaType = MediaType.APPLICATION_XML_TYPE;
            }

            log.info("Accessing " + method + " " + url);
            Response response = null;
            if(body != null){
                response = target.request().method(method, javax.ws.rs.client.Entity.entity(body, bodyMediaType));
            } else {
                response = target.request().method(method);
            }
            return response;
        } catch (Exception e) {
            throw new WebgephiClientException("Resource could not be requested: " + resource, e);
        }
    }

    private String getEndUserURL(String method, String url, String consumerKey, String consumerSecret, String accessKey, String accessSecret) throws Exception {
        OAuthMessage message = new OAuthMessage(method, url, Collections.<Map.Entry>emptyList());
        OAuthConsumer consumer = new OAuthConsumer(/*getCallbackURI()*/ null, consumerKey, consumerSecret, null);
        OAuthAccessor accessor = new OAuthAccessor(consumer);
        accessor.accessToken = accessKey;
        accessor.tokenSecret = accessSecret;
        message.addParameter(OAuth.OAUTH_TOKEN, accessKey);
        message.addRequiredParameters(accessor);
        return OAuth.addParameters(message.URL, message.getParameters());
    }

    public Token getAccessToken() {
        return accessToken;
    }
}
