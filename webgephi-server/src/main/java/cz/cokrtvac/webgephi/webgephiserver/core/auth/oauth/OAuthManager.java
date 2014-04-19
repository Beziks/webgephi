package cz.cokrtvac.webgephi.webgephiserver.core.auth.oauth;

import cz.cokrtvac.webgephi.webgephiserver.core.auth.LoginManager;
import cz.cokrtvac.webgephi.webgephiserver.core.ejb.OAuthDAO;
import cz.cokrtvac.webgephi.webgephiserver.core.ejb.UserDAO;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthAccessTokenEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthConsumerEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthRequestTokenEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.UserEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.NoSuchRoleException;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role;
import org.jboss.errai.security.shared.AuthenticationService;
import org.jboss.resteasy.auth.oauth.OAuthConsumer;
import org.jboss.resteasy.auth.oauth.OAuthException;
import org.jboss.resteasy.auth.oauth.OAuthRequestToken;
import org.jboss.resteasy.auth.oauth.OAuthToken;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 23.1.14
 * Time: 22:53
 */
@Stateless
public class OAuthManager {
    private static final Role DEFAULT_CONSUMER_ROLE = Role.CLIENT_APP;
    private static final Set<Role> FORBIDDEN_ROLES = new HashSet<Role>(Arrays.asList(new Role[]{Role.ADMIN, Role.USER, Role.PROFILE_WRITE}));

    @Resource
    private SessionContext sessionContext;

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private OAuthDAO oAuthDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private LoginManager loginManager;

    @Inject
    private AuthenticationService authenticationService;

    // REQUEST TOKEN ====================================================================================
    public OAuthToken makeRequestToken(String consumerKey, String callback, String[] scopes, String[] permissions) throws OAuthException {

        OAuthRequestTokenEntity e = new OAuthRequestTokenEntity();
        e.setToken(makeRandomString());
        e.setSecret(makeRandomString());
        e.setConsumer(oAuthDAO.getConsumerEntity(consumerKey));
        e.setCallback(callback);

        // Set requested scopes (consumer default + message scopes)
        try {
            Set<Role> requestedScopes = Role.parse(scopes);
            Set<Role> consumerDefault = Role.parse(oAuthDAO.getConsumerEntity(consumerKey).getScopes());
            requestedScopes.addAll(consumerDefault);

            for (Role r : requestedScopes) {
                if (FORBIDDEN_ROLES.contains(r)) {
                    new OAuthException(HttpURLConnection.HTTP_BAD_REQUEST, "Forbidden scope requested: " + r.name());
                }
                e.getScopes().add(r.name());
            }
        } catch (NoSuchRoleException ex) {
            new OAuthException(HttpURLConnection.HTTP_BAD_REQUEST, "Invalid scope requested: " + ex.getInvalidRole());
        }

        em.persist(e);

        return new OAuthRequestToken(e.getToken(), e.getSecret(), callback, scopes, permissions, -1, getConsumer(consumerKey));
    }

    public OAuthRequestToken getRequestToken(String consumerKey, String requestToken) throws OAuthException {
        OAuthRequestTokenEntity entity = oAuthDAO.getRequestTokenEntity(requestToken);
        if (entity == null) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "No such request token: " + requestToken);
        }

        OAuthRequestToken token = new OAuthRequestToken(
                entity.getToken(),
                entity.getSecret(),
                entity.getCallback(),
                entity.getScopes().toArray(new String[entity.getScopes().size()]),
                new String[]{},
                -1,
                getConsumer(entity.getConsumer().getKey()));
        token.setVerifier(entity.getVerifier());
        return token;
    }

    public String authoriseRequestToken(String consumerKey, String requestToken) throws OAuthException {
        String verifier = makeRandomString();
        OAuthRequestTokenEntity requestTokenEntity = oAuthDAO.getRequestTokenEntity(requestToken);

        if (requestTokenEntity == null || !requestTokenEntity.getConsumer().getKey().equals(consumerKey)) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Request token for the consumer " + consumerKey + " can not be authorized. No such token: " + requestToken);
        }
        Set<Role> requestedScopes;

        try {
            requestedScopes = Role.parse(requestTokenEntity.getScopes());
        } catch (NoSuchRoleException e) {
            throw new OAuthException(HttpURLConnection.HTTP_BAD_REQUEST, "Invalid scope requested: " + e.getInvalidRole());
        }

        String logged = authenticationService.getUser().getLoginName();
        Set<Role> userRoles = loginManager.getUserRoles(logged);

        for (Role r : requestedScopes) {
            if (FORBIDDEN_ROLES.contains(r)) {
                new OAuthException(HttpURLConnection.HTTP_CONFLICT, "Forbidden scope requested: " + r.name());
            }
            if (!userRoles.contains(r)) {
                new OAuthException(HttpURLConnection.HTTP_CONFLICT, "This user cannot provide such scope: " + r.name());
            }
        }

        // Set user to token
        requestTokenEntity.setUser(userDAO.getUserEntity(logged));

        requestTokenEntity.setVerifier(verifier);
        return verifier;
    }

    private OAuthRequestToken verifyAndRemoveRequestToken(String consumerKey, String requestToken, String verifier) throws OAuthException {
        OAuthRequestTokenEntity entity = oAuthDAO.getRequestTokenEntity(requestToken);

        if (consumerKey == null || !consumerKey.equals(entity.getConsumer().getKey())) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Invalid customer key " + consumerKey);
        }

        // check the verifier, which is only set when the request token was accepted
        if (verifier == null || !verifier.equals(entity.getVerifier())) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "Invalid verifier code for token " + requestToken);
        }

        OAuthRequestToken token = getRequestToken(consumerKey, requestToken);
        em.remove(entity);
        return token;
    }

    // ACCESS TOKEN ====================================================================================
    public OAuthToken makeAccessToken(String consumerKey, String requestTokenKey, String verifier) throws OAuthException {
        OAuthRequestTokenEntity requestTokenEntity = oAuthDAO.getRequestTokenEntity(requestTokenKey);

        OAuthConsumerEntity consumerEntity = oAuthDAO.getConsumerEntity(consumerKey);
        UserEntity userEntity = requestTokenEntity.getUser();
        Set<String> scopes = requestTokenEntity.getScopes();

        OAuthRequestToken requestToken = verifyAndRemoveRequestToken(consumerKey, requestTokenKey, verifier);

        OAuthAccessTokenEntity entity = getOrCreateAccessToken(consumerEntity, userEntity, scopes);

        return new OAuthToken(
                entity.getToken(),
                entity.getSecret(),
                requestToken.getScopes(),
                requestToken.getPermissions(),
                -1,
                requestToken.getConsumer()
        );
    }

    public OAuthToken getAccessToken(String consumerKey, String accessToken) throws OAuthException {
        OAuthAccessTokenEntity entity = oAuthDAO.getAccessTokenEntity(accessToken);

        if (entity == null || !entity.getConsumer().getKey().equals(consumerKey)) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "No such token for " + consumerKey + ": " + accessToken);
        }

        return new OAuthToken(
                entity.getToken(),
                entity.getSecret(),
                entity.getScopes().toArray(new String[entity.getScopes().size()]),
                new String[]{},
                -1,
                getConsumer(consumerKey));
    }

    /**
     * If exactly the same token already exists (same consumer, user, scopes), return that.
     * Otherwise create a new one.
     */
    private OAuthAccessTokenEntity getOrCreateAccessToken(OAuthConsumerEntity consumerEntity, UserEntity userEntity, Set<String> scopes) {
        for(OAuthAccessTokenEntity accessTokenEntity : userEntity.getAccessTokens()){
            if(accessTokenEntity.getConsumer().getKey().equals(consumerEntity.getKey())){
                if(accessTokenEntity.getScopes().equals(scopes)){
                    log.debug("Same access token exists, reusing that one");
                    return accessTokenEntity;
                }
            }
        }

        log.debug("Creating new access token");
        OAuthAccessTokenEntity entity = new OAuthAccessTokenEntity();
        entity.setToken(makeRandomString());
        entity.setSecret(makeRandomString());
        entity.setConsumer(consumerEntity);
        entity.setScopes(new HashSet<String>(scopes));
        entity.setUser(userEntity);
        em.persist(entity);
        return entity;
    }

    // CONSUMER ====================================================================================
    public OAuthConsumer getConsumer(String consumerKey) throws OAuthException {
        OAuthConsumerEntity consumerEntity = oAuthDAO.getConsumerEntity(consumerKey);
        if (consumerEntity == null) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED, "No such consumer: " + consumerKey);
        }

        OAuthConsumer consumer = new OAuthConsumer(
                consumerEntity.getKey(),
                consumerEntity.getSecret(),
                consumerEntity.getApplicationName(),
                consumerEntity.getConnectUri(),
                new String[]{}
        );

        consumer.setScopes(consumerEntity.getScopes().toArray(new String[consumerEntity.getScopes().size()]));
        return consumer;
    }

    // CONSUMER REGISTRATION (not used) ====================================================================================
    public OAuthConsumer registerConsumer(String consumerKey, String displayName, String connectURI) throws OAuthException {
        String secret = makeRandomString();

        OAuthConsumerEntity e = new OAuthConsumerEntity();
        e.setKey(consumerKey);
        e.setApplicationName(displayName);
        e.setConnectUri(connectURI);

        em.persist(e);
        em.flush();

        return new OAuthConsumer(consumerKey, secret, displayName, connectURI);
    }

    public void registerConsumerScopes(String consumerKey, String[] scopes) throws OAuthException {
        OAuthConsumerEntity e = oAuthDAO.getConsumerEntity(consumerKey);
        e.setScopes(new HashSet<String>(Arrays.asList(scopes)));
    }


    public void registerConsumerPermissions(String consumerKey, String[] permissions) throws OAuthException {
        OAuthConsumerEntity e = oAuthDAO.getConsumerEntity(consumerKey);
    }

    // PERMISSIONS ====================================================================================

    /**
     * Convert !!! SCOPEs !!! to roles. We dont use permissions at all...
     *
     * @param permissions
     * @return
     */
    public Set<String> convertPermissionsToRoles(String[] permissions) {
        Set<String> out = new HashSet<String>();
        Set<Role> roles = null;
        try {
            roles = Role.parse(Arrays.asList(permissions));
        } catch (NoSuchRoleException e) {
            throw new WebApplicationException(new OAuthException(HttpURLConnection.HTTP_CONFLICT, "Invalid scope requested: " + e.getInvalidRole()), HttpURLConnection.HTTP_BAD_REQUEST);
        }
        roles.add(DEFAULT_CONSUMER_ROLE);
        for (Role r : roles) {
            if (FORBIDDEN_ROLES.contains(r)) {
                continue;
            }
            out.add(r.name());
        }
        return out;
    }

    private static String makeRandomString() {
        return UUID.randomUUID().toString();
    }


}
