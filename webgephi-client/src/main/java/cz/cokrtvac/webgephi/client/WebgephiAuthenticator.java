package cz.cokrtvac.webgephi.client;

import cz.cokrtvac.webgephi.client.util.ClientRequestFactory;
import cz.cokrtvac.webgephi.client.util.UrlUtil;
import net.oauth.*;
import org.jboss.resteasy.auth.oauth.OAuthUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 28.1.14
 * Time: 17:49
 */
public class WebgephiAuthenticator {
    private Logger log = LoggerFactory.getLogger(WebgephiAuthenticator.class);

    // Consumer setting
    private String consumerKey;
    private String consumerSecret;

    // Server setting
    private String serverBase;
    private String requestTokenEndpoint;
    private String requestTokenAuthorizationEndpoint;
    private String accessTokenEndpoint;

    // Client setting
    private String callbackUrl;

    // Tokens
    private Token requestToken;
    private Token accessToken;

    /**
     * @param consumerKey                           your app consumer key as registered on server
     * @param consumerSecret                        your app secret (password)
     * @param serverBaseUrl                         base url of webgephi server on are communicating with
     * @param requestTokenEndpointPath              relative path on server to obtain unauthorized request token (www.webgephi.cz/oauth/requestToken -> oauth/requestToken)
     * @param requestTokenAuthorizationEndpointPath relative path on server to authorize request token (user will be redirected there)
     * @param accessTokenEndpointPath               relative path on server to exchange authorized request token for access token
     * @param clientBaseUrl                         base url of your client app
     * @param callbackEndpointPath                  relative path to clientBaseUrl. User will be redirected there after request token authorization. Or after he denies authorization or authorization fail (with proper parameters).
     */
    public WebgephiAuthenticator(
            String consumerKey, String consumerSecret,
            String serverBaseUrl, String requestTokenEndpointPath, String requestTokenAuthorizationEndpointPath, String accessTokenEndpointPath,
            String clientBaseUrl, String callbackEndpointPath
    ) {

        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;

        this.serverBase = serverBaseUrl;
        this.requestTokenEndpoint = UrlUtil.concat(this.serverBase, requestTokenEndpointPath);
        this.requestTokenAuthorizationEndpoint = UrlUtil.concat(this.serverBase, requestTokenAuthorizationEndpointPath);
        this.accessTokenEndpoint = UrlUtil.concat(this.serverBase, accessTokenEndpointPath);

        this.callbackUrl = UrlUtil.concat(clientBaseUrl, callbackEndpointPath);
    }

    /**
     * Like full constructor with some default values:
     * - requestTokenEndpointPath = oauth/requestToken
     * - requestTokenAuthorizationEndpointPath = oauth/authorization
     * - accessTokenEndpointPath = oauth/accessToken
     * - callbackEndpointPath = oauth/callback (default of OAuthTokenAuthorizationCallback servlet)
     *
     * @param consumerKey
     * @param consumerSecret
     * @param serverBaseUrl
     * @param clientBaseUrl
     */
    public WebgephiAuthenticator(
            String consumerKey, String consumerSecret,
            String serverBaseUrl,
            String clientBaseUrl
    ) {
        this(
                consumerKey, consumerSecret,
                serverBaseUrl, "oauth/requestToken", "oauth/authorization", "oauth/accessToken",
                clientBaseUrl, "oauth/callback"
        );
    }

    /**
     * Obtains an request token.
     * To authorize token, you have to redirect user to url from return statement.
     * After authorization, user will be redirected back to callbackUrl.
     * In AuthorizationCallback class, you have to set verifier and obtain access token: obtainAccessToken()
     *
     * @param scopes
     * @return url address where you have to redirect user.
     * @throws WebgephiAuthenticatorException
     */
    public String authorize(String... scopes) throws WebgephiAuthenticatorException {
        if (scopes == null) {
            throw new IllegalArgumentException("Scopes have to be defined");
        }

        StringBuilder sb = new StringBuilder();
        for (String s : scopes) {

        }

        try {
            requestToken = getRequestToken(consumerKey, consumerSecret, callbackUrl, scopes);
            log.debug("Request token obtained");
        } catch (Exception e) {
            throw new WebgephiAuthenticatorException("Request token could not be obtained", e);
        }

        try {
            String authorizationUrl = getAuthorizationUrl(requestToken);
            return authorizationUrl;
        } catch (Exception e) {
            throw new WebgephiAuthenticatorException("Request token authorization url could not be created", e);
        }
    }

    /**
     * After user authorizes your request token, call this method to change it for access token.
     * You should call this method from AuthorizationCallback class
     *
     * @param verifier
     * @return
     * @throws WebgephiAuthenticatorException
     */
    public Token obtainAccessToken(String verifier) throws WebgephiAuthenticatorException {
        if (requestToken == null || verifier == null) {
            throw new IllegalStateException("You have to call authorize method first");
        }

        requestToken.setVerifier(verifier);
        try {
            accessToken = getAccessToken();
            return accessToken;
        } catch (Exception e) {
            throw new WebgephiAuthenticatorException("Access token could not be obtained", e);
        }
    }

    // REQUEST TOKEN ==============================================================================================================
    private Token getRequestToken(String consumerKey, String consumerSecret, String callbackURI, String[] scopes) throws Exception {
        String url = getRequestURL(scopes);
        log.info("Request token URL: " + url);

        ClientRequest request = ClientRequestFactory.create(url);
        ClientResponse<String> response = request.get(String.class);
        if (HttpResponseCodes.SC_OK != response.getStatus()) {
            log.warn(response.getStatus() + " : " + response.getEntity());
            response.releaseConnection();
            throw new RuntimeException("Request token can not be obtained");
        }
        // check that we got all tokens
        Map<String, String> tokens = getTokens(response.getEntity());
        if (tokens.size() != 3
                || !tokens.containsKey(OAuth.OAUTH_TOKEN)
                || !(tokens.get(OAuth.OAUTH_TOKEN).length() > 0)
                || !tokens.containsKey(OAuth.OAUTH_TOKEN_SECRET)
                || !(tokens.get(OAuth.OAUTH_TOKEN_SECRET).length() > 0)
                || !tokens.containsKey(OAuthUtils.OAUTH_CALLBACK_CONFIRMED_PARAM)
                || !tokens.get(OAuthUtils.OAUTH_CALLBACK_CONFIRMED_PARAM).equals("true")) {
            throw new RuntimeException("Wrong request token details");
        }

        return new Token(consumerKey, consumerSecret, tokens.get(OAuth.OAUTH_TOKEN), tokens.get(OAuth.OAUTH_TOKEN_SECRET));
    }

    private String getRequestURL(String[] scopes) throws OAuthException, IOException, URISyntaxException {
        OAuthMessage message = new OAuthMessage("GET", requestTokenEndpoint, Collections.<Map.Entry>emptyList());
        OAuthConsumer consumer = new OAuthConsumer(callbackUrl, consumerKey, consumerSecret, null);
        OAuthAccessor accessor = new OAuthAccessor(consumer);
        message.addParameter(OAuth.OAUTH_CALLBACK, consumer.callbackURL);
        for (String s : scopes) {
            message.addParameter("xoauth_scope", s);
        }
        message.addRequiredParameters(accessor);
        return OAuth.addParameters(message.URL, message.getParameters());
    }

    // REQUEST TOKEN AUTHORIZATION ==============================================================================================================
    private String getAuthorizationUrl(Token requestToken) throws Exception {
        String authorizationUrl = getAuthorizationURL(requestToken);
        log.info("Authorization URL: " + authorizationUrl);
        return authorizationUrl;
    }

    private String getAuthorizationURL(Token requestToken) throws Exception {
        List<OAuth.Parameter> parameters = new ArrayList<OAuth.Parameter>();
        parameters.add(new OAuth.Parameter(OAuth.OAUTH_TOKEN, requestToken.getToken()));
        return OAuth.addParameters(requestTokenAuthorizationEndpoint, parameters);
    }

    // ACCESS TOKEN ==============================================================================================================
    private Token getAccessToken() throws Exception {
        String url = getAccessURL(consumerKey, consumerSecret, requestToken.getToken(), requestToken.getSecret(), requestToken.getVerifier());
        ClientRequest request = ClientRequestFactory.create(url);
        ClientResponse<String> response = request.get(String.class);
        if (HttpResponseCodes.SC_OK != response.getStatus()) {
            response.releaseConnection();
            throw new RuntimeException("Access token can not be obtained");
        }
        // check that we got all tokens
        Map<String, String> tokens = getTokens(response.getEntity());
        if (tokens.size() != 2
                || !tokens.containsKey(OAuth.OAUTH_TOKEN)
                || !(tokens.get(OAuth.OAUTH_TOKEN).length() > 0)
                || !tokens.containsKey(OAuth.OAUTH_TOKEN_SECRET)
                || !(tokens.get(OAuth.OAUTH_TOKEN_SECRET).length() > 0)) {
            throw new RuntimeException("Wrong access token details");
        }

        return new Token(consumerKey, consumerSecret, tokens.get(OAuth.OAUTH_TOKEN), tokens.get(OAuth.OAUTH_TOKEN_SECRET));
    }

    private String getAccessURL(String consumerKey, String consumerSecret, String requestKey, String requestSecret, String verifier) throws Exception {
        OAuthMessage message = new OAuthMessage("GET", accessTokenEndpoint, Collections.<Map.Entry>emptyList());
        OAuthConsumer consumer = new OAuthConsumer("http://callback.net", consumerKey, consumerSecret, null);
        OAuthAccessor accessor = new OAuthAccessor(consumer);
        accessor.requestToken = requestKey;
        accessor.tokenSecret = requestSecret;
        message.addParameter(OAuthUtils.OAUTH_VERIFIER_PARAM, verifier);
        message.addParameter(OAuth.OAUTH_TOKEN, requestKey);
        message.addRequiredParameters(accessor);
        return OAuth.addParameters(message.URL, message.getParameters());
    }

    // UTILS ==============================================================================================================
    private Map<String, String> getTokens(String response) throws Exception {
        return OAuth.newMap(OAuth.decodeForm(response));
    }
}
