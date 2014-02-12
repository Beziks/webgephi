package cz.cokrtvac.webgephi.webgephiserver.gwt.server;

import cz.cokrtvac.webgephi.webgephiserver.core.auth.oauth.OAuthManager;
import cz.cokrtvac.webgephi.webgephiserver.core.ejb.OAuthDAO;
import cz.cokrtvac.webgephi.webgephiserver.core.ejb.UserDAO;
import cz.cokrtvac.webgephi.webgephiserver.core.util.security_annotation.Secure;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.OAuthService;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthAccessTokenEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthRequestTokenEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.UserEntity;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.AuthenticationService;
import org.jboss.resteasy.auth.oauth.OAuthException;
import org.slf4j.Logger;

import javax.inject.Inject;

@Service
public class OAuthServiceImpl implements OAuthService {
    @Inject
    private Logger log;

    @Inject
    AuthenticationService authenticationService;

    @Inject
    private UserDAO userDAO;

    @Inject
    private OAuthDAO oAuthDAO;

    @Inject
    private OAuthManager oAuthManager;


    @Secure
    @Override
    public OAuthRequestTokenEntity getRequestToken(String requestToken) {
        try {
            OAuthRequestTokenEntity e = oAuthDAO.getRequestTokenWithConsumer(requestToken);
            // security
            e.setSecret(null);
            // We dont need it
            e.getConsumer().setSecret(null);
            e.getConsumer().setRequestTokens(null);
            e.getConsumer().setAccessTokens(null);
            e.getConsumer().setUser(null);

            if (e.getUser() != null) {
                e.getUser().setGraphs(null);
                e.getUser().setAccessTokens(null);
                e.getUser().setRequestTokens(null);
                e.getUser().setClientAppEntity(null);
            }
            return e;
        } catch (RuntimeException e) {
            log.error("OAuthServiceImpl call failed.", e);
            throw e;
        }
    }

    /**
     * @param consumerKey
     * @param requestTokenEntity with a verifier. Should be send back to consumer (params OAuth.OAUTH_TOKEN and OAuth.OAUTH_VERIFIER)
     * @return
     */
    @Secure
    @Override
    public OAuthRequestTokenEntity authorizeRequestToken(String consumerKey, OAuthRequestTokenEntity requestTokenEntity) {
        try {
            log.debug("Authorizing token: " + requestTokenEntity.getToken());
            String callback = requestTokenEntity.getCallback();
            log.debug("Token callback: " + callback);

            String verifier = null;
            try {
                verifier = oAuthManager.authoriseRequestToken(consumerKey, requestTokenEntity.getToken());
            } catch (OAuthException e) {
                log.error("Authorization of request token failed.", e);
                return null;
            }

            if (callback == null) {
                log.debug("Callback is null, data should be shown on page");
                //OAuthUtils.sendValues(resp, OAuth.OAUTH_TOKEN, requestTokenEntity.getToken(), OAuth.OAUTH_VERIFIER, verifier);
                //resp.setStatus(HttpURLConnection.HTTP_OK);
            } else {
                log.debug("Callback is not null, data should be send on callback url");
            }
            // Verifier is set now
            return getRequestToken(requestTokenEntity.getToken());
        } catch (RuntimeException e) {
            log.error("OAuthServiceImpl call failed.", e);
            throw e;
        }
    }

    @Secure
    @Override
    public UserEntity getUserWithAuthorizedAccessTokens() {
        try {
            // Get logged user
            String username = authenticationService.getUser().getLoginName();
            UserEntity e = oAuthDAO.getUserWithAuthorizedAccessTokens(username);
            e.setRequestTokens(null);
            e.setGraphs(null);
            e.setClientAppEntity(null);
            for (OAuthAccessTokenEntity t : e.getAccessTokens()) {
                t.getConsumer().setSecret(null);
                t.getConsumer().setRequestTokens(null);
                t.getConsumer().setAccessTokens(null);
                t.getConsumer().setUser(null);
            }

            return e;
        } catch (RuntimeException e) {
            log.error("OAuthServiceImpl call failed.", e);
            throw e;
        }
    }

    @Secure
    @Override
    public boolean deleteAccessTokens(String accessTokenEntity) {
        try {
            String username = authenticationService.getUser().getLoginName();
            OAuthAccessTokenEntity e = oAuthDAO.getAccessTokenEntity(accessTokenEntity);
            if (!e.getUser().getUsername().equals(username)) {
                return false;
            }
            oAuthDAO.deleteAccessToken(accessTokenEntity);
            return true;
        } catch (RuntimeException e) {
            log.error("OAuthServiceImpl call failed.", e);
            return false;
        }
    }
}
