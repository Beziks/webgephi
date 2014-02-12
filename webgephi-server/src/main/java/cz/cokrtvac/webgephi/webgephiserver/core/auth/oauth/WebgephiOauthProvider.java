package cz.cokrtvac.webgephi.webgephiserver.core.auth.oauth;

import cz.cokrtvac.webgephi.api.util.Log;
import cz.cokrtvac.webgephi.webgephiserver.core.InitializationException;
import org.jboss.resteasy.auth.oauth.*;
import org.slf4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.HttpURLConnection;
import java.util.Set;

/**
 * OAuthDBProvider that keeps all data in DB.
 */
public class WebgephiOauthProvider implements OAuthProvider {
    private static Logger log = Log.get(WebgephiOauthProvider.class);

    /**
     * EJB which do all work
     */
    private OAuthManager oAuthManager;

    public WebgephiOauthProvider() {
        try {
            InitialContext initialContext = new InitialContext();
            oAuthManager = (OAuthManager) initialContext.lookup("java:module/OAuthManager");
        } catch (NamingException e) {
            log.error("Lookup for EJB OAuthManager failed", e);
            throw new InitializationException("Lookup for EJB OAuthManager failed", e);
        }

    }

    /**
     * #1# Create unauthorized request token
     *
     * @param consumerKey
     * @param callback
     * @param scopes
     * @param permissions
     * @return
     * @throws org.jboss.resteasy.auth.oauth.OAuthException
     */
    @Override
    public OAuthToken makeRequestToken(String consumerKey, String callback, String[] scopes, String[] permissions) throws OAuthException {
        try {
            return oAuthManager.makeRequestToken(consumerKey, callback, scopes, permissions);
        } catch (OAuthException oe) {
            throw oe;
        } catch (Exception ex) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Request token for the consumer with `key` " + consumerKey + " can not be created");
        }
    }

    /**
     * #2# Authorize request token - set verifier
     *
     * @param consumerKey
     * @param requestToken
     * @return
     * @throws org.jboss.resteasy.auth.oauth.OAuthException
     */
    @Override
    public String authoriseRequestToken(String consumerKey, String requestToken) throws OAuthException {
        try {
            return oAuthManager.authoriseRequestToken(consumerKey, requestToken);
        } catch (OAuthException oe) {
            throw oe;
        } catch (Exception ex) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Request token for the consumer " + consumerKey + " can not be authorized");
        }
    }

    @Override
    public void checkTimestamp(OAuthToken token, long timestamp) throws OAuthException {
        // never expires
    }

    @Override
    public OAuthToken getAccessToken(String consumerKey, String accessToken) throws OAuthException {
        try {
            return oAuthManager.getAccessToken(consumerKey, accessToken);
        } catch (OAuthException oe) {
            throw oe;
        } catch (Exception ex) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "No such consumer access token for " + consumerKey + ": " + consumerKey);
        }
    }

    /**
     * #0#
     * Find consumer with this key
     *
     * @param consumerKey - consumer name (key) as set in database (during consumer registration)
     * @return
     * @throws org.jboss.resteasy.auth.oauth.OAuthException
     */
    @Override
    public OAuthConsumer getConsumer(String consumerKey) throws OAuthException {
        try {
            return oAuthManager.getConsumer(consumerKey);
        } catch (OAuthException oe) {
            throw oe;
        } catch (Exception ex) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "No such consumer key " + consumerKey);
        }
    }

    /**
     * @return Name of security realm
     */
    @Override
    public String getRealm() {
        return "default";
    }

    @Override
    public OAuthRequestToken getRequestToken(String consumerKey, String requestToken) throws OAuthException {
        try {
            return oAuthManager.getRequestToken(consumerKey, requestToken);
        } catch (OAuthException oe) {
            throw oe;
        } catch (Exception ex) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "No such consumer key " + consumerKey);
        }
    }

    @Override
    public OAuthToken makeAccessToken(String consumerKey, String requestTokenKey, String verifier) throws OAuthException {
        try {
            return oAuthManager.makeAccessToken(consumerKey, requestTokenKey, verifier);
        } catch (OAuthException oe) {
            throw oe;
        } catch (Exception ex) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Request token for the consumer with key " + consumerKey + " can not be created");
        }
    }

    @Override
    public OAuthConsumer registerConsumer(String consumerKey, String displayName, String connectURI) throws OAuthException {
        try {
            return oAuthManager.registerConsumer(consumerKey, displayName, connectURI);
        } catch (OAuthException oe) {
            throw oe;
        } catch (Exception ex) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Consumer with key " + consumerKey + " can not be created");
        }
    }

    @Override
    public void registerConsumerScopes(String consumerKey, String[] scopes) throws OAuthException {
        try {
            oAuthManager.registerConsumerScopes(consumerKey, scopes);
        } catch (OAuthException oe) {
            throw oe;
        } catch (Exception ex) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Scopes for the consumer with key " + consumerKey + " can not be registered");
        }

    }

    @Override
    public void registerConsumerPermissions(String consumerKey, String[] permissions) throws OAuthException {
        try {
            oAuthManager.registerConsumerPermissions(consumerKey, permissions);
        } catch (OAuthException oe) {
            throw oe;
        } catch (Exception ex) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Permissions for the consumer with key " + consumerKey + " can not be registered");
        }

    }

    @Override
    public Set<String> convertPermissionsToRoles(String[] permissions) {
        return oAuthManager.convertPermissionsToRoles(permissions);
    }
}


