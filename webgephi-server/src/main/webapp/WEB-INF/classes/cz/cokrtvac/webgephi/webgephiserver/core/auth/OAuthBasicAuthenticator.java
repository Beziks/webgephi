package cz.cokrtvac.webgephi.webgephiserver.core.auth;

import cz.cokrtvac.webgephi.api.util.Log;
import cz.cokrtvac.webgephi.webgephiserver.core.InitializationException;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.UserEntity;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.authenticator.BasicAuthenticator;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.jboss.resteasy.auth.oauth.*;
import org.picketlink.idm.credential.Credentials;
import org.slf4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class OAuthBasicAuthenticator extends AuthenticatorBase {

    private static final String INFO = "cz.cokrtvac.webgephi.webgephiserver.core.auth.OAuthBasicAuthenticator/1.0";
    private static final Set<String> SUPPORTED_AUTH_METHODS = new HashSet<String>(Arrays.asList("oauth", "basic", "oauth+basic", "basic+oauth"));

    private BasicAuthenticator ba = new BasicAuthenticator();

    private String oauthProviderName;

    private OAuthProvider oauthProvider;
    private OAuthValidator validator;

    private Realm originalRealm;

    private LoginManager loginManager;
    private Logger log = Log.get(getClass());

    private LoginManager getLoginManager() {
        if (loginManager == null) {
            try {
                InitialContext initialContext = new InitialContext();
                loginManager = (LoginManager) initialContext.lookup("java:module/LoginManager");
            } catch (NamingException e) {
                log.error("Lookup forLoginManager failed", e);
                throw new InitializationException("Lookup for LoginManager failed", e);
            }
        }
        return loginManager;
    }

    public OAuthBasicAuthenticator() {
        super();
    }

    public String getInfo() {
        return INFO;
    }

    public void setOauthProviderName(String oauthProviderName) {
        this.oauthProviderName = oauthProviderName;
    }

    public String getOauthProviderName() {
        return oauthProviderName;
    }

    @Override
    public void setContainer(Container container) {
        super.setContainer(container);
        ba.setContainer(container);
        originalRealm = container.getRealm();
    }

    @Override
    protected boolean authenticate(Request request, HttpServletResponse response, LoginConfig config) throws IOException {
        String authMethod = config.getAuthMethod();
        if (!SUPPORTED_AUTH_METHODS.contains(authMethod.toLowerCase())) {
            throw new SecurityException("Unsupported auth method : " + authMethod);
        }

        boolean useOauth = true;
        OAuthMessage message = OAuthUtils.readMessage(request);
        try {
            message.requireParameters(OAuth.OAUTH_CONSUMER_KEY);
        } catch (OAuthProblemException e) {
            useOauth = false;
        }

        String authorizationHeader = request.getHeader("Authorization");

        if (useOauth) {
            try {
                doAuthenticateOAuth(request, response);
            } catch (ServletException ex) {
                throw new IOException(ex);
            }
        } else {
            context.setRealm(new HttpBasicRealm());
            return ba.authenticate(request, response, config);
        }
        return false;
    }


    @Override
    public void start() throws LifecycleException {
        super.start();

        try {
            Class<?> providerClass = Class.forName(oauthProviderName);
            oauthProvider = (OAuthProvider) providerClass.newInstance();
            validator = new OAuthValidator(oauthProvider);
        } catch (Exception ex) {
            throw new LifecycleException("In memory OAuth DB can not be created " + ex.getMessage());
        }
    }


    protected void doAuthenticateOAuth(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        OAuthMessage message = OAuthUtils.readMessage(request);
        try {
            message.requireParameters(OAuth.OAUTH_CONSUMER_KEY,
                    OAuth.OAUTH_SIGNATURE_METHOD,
                    OAuth.OAUTH_SIGNATURE,
                    OAuth.OAUTH_TIMESTAMP,
                    OAuth.OAUTH_NONCE);

            String consumerKey = message.getParameter(OAuth.OAUTH_CONSUMER_KEY);
            org.jboss.resteasy.auth.oauth.OAuthConsumer consumer = oauthProvider.getConsumer(consumerKey);

            OAuthToken accessToken = null;
            String accessTokenString = message.getParameter(OAuth.OAUTH_TOKEN);

            if (accessTokenString != null) {
                accessToken = oauthProvider.getAccessToken(consumer.getKey(), accessTokenString);
                validateRequestWithAccessToken(request, message, accessToken, validator, consumer);
            } else {
                throw new OAuthException(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(), "Access token required");
            }

            createPrincipalAndRoles(request, consumer, accessToken);
            getNext().invoke((Request) request, (Response) response);

        } catch (OAuthException x) {
            OAuthUtils.makeErrorResponse(response, x.getMessage(), x.getHttpCode(), oauthProvider);
        } catch (OAuthProblemException x) {
            OAuthUtils.makeErrorResponse(response, x.getProblem(), OAuthUtils.getHttpCode(x), oauthProvider);
        } catch (Exception x) {
            OAuthUtils.makeErrorResponse(response, x.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR, oauthProvider);
        }

    }

    public static void validateRequestWithAccessToken(
            HttpServletRequest request,
            OAuthMessage message,
            OAuthToken accessToken,
            OAuthValidator validator,
            org.jboss.resteasy.auth.oauth.OAuthConsumer consumer) throws Exception {

        net.oauth.OAuthConsumer _consumer = new net.oauth.OAuthConsumer(null, consumer.getKey(), accessToken.getConsumer().getSecret(), null);
        OAuthAccessor accessor = new OAuthAccessor(_consumer);
        accessor.accessToken = accessToken.getToken();
        accessor.tokenSecret = accessToken.getSecret();

        // validate the message
        validator.validateMessage(message, accessor, accessToken);
    }

    protected void createPrincipalAndRoles(HttpServletRequest request,
                                           org.jboss.resteasy.auth.oauth.OAuthConsumer consumer,
                                           OAuthToken accessToken) {

        Set<String> roles = oauthProvider.convertPermissionsToRoles(accessToken.getScopes());
        Realm realm = new OAuthRealm(roles);
        context.setRealm(realm);
        UserEntity entity = getLoginManager().getUserOfAccessTokenEntity(accessToken.getToken());

        // We set username of regular user (not client app) who authorized access
        final Principal principal = new GenericPrincipal(realm, entity.getUsername(), "", new ArrayList<String>(roles));

        ((Request) request).setUserPrincipal(principal);
        ((Request) request).setAuthType("OAuth");
    }

    private class OAuthRealm extends RealmBase {

        private Set<String> roles;

        public OAuthRealm(Set<String> roles) {
            this.roles = roles;
        }

        @Override
        protected String getName() {
            return "OAuthRealm";
        }

        @Override
        protected String getPassword(String username) {
            return "";
        }

        @Override
        protected Principal getPrincipal(String username) {
            GenericPrincipal p = new GenericPrincipal(this, username, "", new ArrayList<String>(roles), null);
            return p;
        }

        @Override
        public boolean hasResourcePermission(Request request, Response response, SecurityConstraint[] constraints, Context context) {
            return true;
        }

        @Override
        public boolean hasRole(Principal principal, String role) {
            return roles.contains(role);
        }
    }

    private class HttpBasicRealm extends RealmBase {
        private Logger log = Log.get(HttpBasicRealm.class);

        Set<String> roles;

        public HttpBasicRealm() {

        }

        @Override
        protected String getName() {
            return "HttpBasicRealm";
        }

        @Override
        protected String getPassword(String username) {
            return null;
        }

        @Override
        public Principal authenticate(String username, String credentials) {
            Credentials.Status s = getLoginManager().validate(username, credentials);
            log.info("Login status: " + s);
            if (s == Credentials.Status.VALID) {
                return getPrincipal(username);
            }
            return null;
        }

        @Override
        protected Principal getPrincipal(String username) {
            return new GenericPrincipal(this, username, "", new ArrayList<String>(getRoles(username)), null);
        }

        @Override
        public boolean hasResourcePermission(Request request, Response response, SecurityConstraint[] constraints, Context context) {
            return true;
        }

        @Override
        public boolean hasRole(Principal principal, String role) {
            return getRoles(principal.getName()).contains(role);
        }

        private Set<String> getRoles(String username) {
            if (roles == null) {
                roles = loginManager.getUserRolesStrings(username);
            }
            return roles;
        }
    }
}
