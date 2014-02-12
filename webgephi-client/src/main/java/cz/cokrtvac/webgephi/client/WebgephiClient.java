package cz.cokrtvac.webgephi.client;

import cz.cokrtvac.webgephi.client.util.ClientRequestFactory;
import cz.cokrtvac.webgephi.client.util.UrlUtil;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Map;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 28.1.14
 * Time: 22:56
 */
public class WebgephiClient {
    private Logger log = LoggerFactory.getLogger(WebgephiClient.class);

    private Token accessToken;
    private String resourceBase;

    /**
     * @param resourceBase - Base address of REST servises on server. E.g. https://webgephi.cz/rest
     * @param accessToken  - Access token obtained from user. If null, only public resources are available (functions)
     */
    public WebgephiClient(String resourceBase, Token accessToken) {
        this.resourceBase = resourceBase;
        this.accessToken = accessToken;
    }

    /**
     * Send authorized GET request to defined url
     *
     * @param targetPath
     * @param type       - class of response
     * @param <T>        - type of response
     * @return
     * @throws Exception
     */
    public <T> ClientResponse<T> get(String targetPath, Class<T> type) throws WebgephiClientException {
        return request("GET", targetPath, type, null, null);
    }

    /**
     * Send authorized PUT request to defined url
     *
     * @param targetPath
     * @param type          - class of response
     * @param bodyMediaType - mediaType of body content, default is application/xml (if null)
     * @param body          - body content of http request
     * @param <T>           - type of response
     * @return
     * @throws WebgephiClientException
     */
    public <T> ClientResponse<T> put(String targetPath, Class<T> type, MediaType bodyMediaType, Object body) throws WebgephiClientException {
        return request("PUT", targetPath, type, bodyMediaType, body);
    }

    /**
     * Send authorized POST request to defined url
     *
     * @param targetPath
     * @param type          - class of response
     * @param bodyMediaType - mediaType of body content, default is 'MediaType.WILDCARD' (if null)
     * @param body          - body content of http request
     * @param <T>           - type of response
     * @return
     * @throws WebgephiClientException
     */
    public <T> ClientResponse<T> post(String targetPath, Class<T> type, MediaType bodyMediaType, Object body) throws WebgephiClientException {
        return request("POST", targetPath, type, bodyMediaType, body);
    }

    private <T> ClientResponse<T> request(String method, String targetPath, Class<T> type, MediaType bodyMediaType, Object body) throws WebgephiClientException {
        String resource = UrlUtil.concat(resourceBase, targetPath);
        log.debug("Requesting resource on url: " + method + " " + resource);

        try {
            String url = getEndUserURL(method, resource, accessToken.getConsumerKey(), accessToken.getConsumerSecret(), accessToken.getToken(), accessToken.getSecret());

            ClientRequest request = ClientRequestFactory.create(url);
            request.setHttpMethod(method);
            if (body != null) {
                if (bodyMediaType == null) {
                    request.body(MediaType.WILDCARD, body);
                } else {
                    request.body(bodyMediaType, body);
                }
            }

            log.info("Accessing " + url);

            BaseClientResponse response = (BaseClientResponse) request.execute();
            response.setReturnType(type);

            log.debug("Response status: " + response.getResponseStatus());
            log.debug("Response data  : " + response.getEntity());
            return response;
        } catch (Exception e) {
            throw new WebgephiClientException("Resource could not be requested: " + resource);
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
