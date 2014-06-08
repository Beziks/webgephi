package cz.cokrtvac.webgephi.webgephiserver.core.auth;

import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.UserEntity;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import org.apache.commons.codec.binary.Base64;
import org.jboss.errai.security.shared.AuthenticationService;
import org.jboss.errai.security.shared.Role;
import org.jboss.resteasy.auth.oauth.*;
import org.picketlink.idm.IdentityManager;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebFilter("/rest/v1/users/*")
public class OAuthBasicAuthenticationFilter extends OAuthFilter {

    @Inject
    private Logger log;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private IdentityManager identityManager;

    @Inject
    private LoginManager loginManager;

    protected OAuthValidator validator;

    public OAuthBasicAuthenticationFilter() {
    }

    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        log.info("Loading oautht validator");
        ServletContext context = config.getServletContext();
        validator = OAuthUtils.getValidator(context, getProvider());
    }

    @Override
    protected void _doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        boolean useOauth = true;
        OAuthMessage message = OAuthUtils.readMessage(request);
        try {
            message.requireParameters(OAuth.OAUTH_CONSUMER_KEY);
        } catch (OAuthProblemException e) {
            useOauth = false;
        }

        String header = request.getHeader("Authorization");

        if (useOauth) {
            log.info("Authorization using oauth");
            oauthDoFilter(request, response, filterChain);
        } else if (header != null && header.startsWith("Basic")) {
            log.info("Authorization using Basic");
            String base64Value = header.substring(6);
            Base64 base64 = new Base64();
            String decoded = new String(base64.decode(base64Value.getBytes()));
            String[] pair = decoded.split(":");
            String username = pair[0];
            String password = pair[1];

            Set<String> roles = loginAndGetRoles(username, password);
            if (roles == null) {
                log.info("Authentication failed, username=" + username);
                response.sendError(Response.Status.UNAUTHORIZED.getStatusCode(), "Unauthorized. Authentication failed for " + username + ". You can log in using OAuth v1 or Basic.");
                return;
            }

            request = createBasicContext(request, username, roles);

            filterChain.doFilter(request, response);
        } else {
            response.addHeader("WWW-Authenticate", "Basic realm=\"default\"");
            response.sendError(Response.Status.UNAUTHORIZED.getStatusCode(), "Unauthorized. You can log in using OAuth v1 or Basic.");
        }
    }

    // Taken from parent class, to disable validation of scopes (it is done in application layer) ----------------------------------------
    protected void oauthDoFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        log.debug("Filtering " + request.getMethod() + " " + request.getRequestURL().toString());

        OAuthMessage message = OAuthUtils.readMessage(request);
        try {
            message.requireParameters(OAuth.OAUTH_CONSUMER_KEY,
                    OAuth.OAUTH_SIGNATURE_METHOD,
                    OAuth.OAUTH_SIGNATURE,
                    OAuth.OAUTH_TIMESTAMP,
                    OAuth.OAUTH_NONCE);

            String consumerKey = message.getParameter(OAuth.OAUTH_CONSUMER_KEY);
            org.jboss.resteasy.auth.oauth.OAuthConsumer consumer = getProvider().getConsumer(consumerKey);

            OAuthToken accessToken = null;
            String accessTokenString = message.getParameter(OAuth.OAUTH_TOKEN);

            if (accessTokenString != null) {
                accessToken = getProvider().getAccessToken(consumer.getKey(), accessTokenString);
                validateRequestWithAccessToken(request, message, accessToken, validator, consumer);
            } else {
                OAuthUtils.validateRequestWithoutAccessToken(request, message, validator, consumer);
            }

            request = createSecurityContext(request, consumer, accessToken);

            // let the request through with the new credentials
            log.debug("doFilter");
            filterChain.doFilter(request, response);

        } catch (OAuthException x) {
            OAuthUtils.makeErrorResponse(response, x.getMessage(), x.getHttpCode(), getProvider());
        } catch (OAuthProblemException x) {
            OAuthUtils.makeErrorResponse(response, x.getProblem(), OAuthUtils.getHttpCode(x), getProvider());
        } catch (Exception x) {
            OAuthUtils.makeErrorResponse(response, x.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR, getProvider());
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

    /**
     * Validates if a given request is a valid 2-leg oAuth request
     */
    public static void validateRequestWithoutAccessToken(
            HttpServletRequest request,
            OAuthMessage message,
            OAuthValidator validator,
            org.jboss.resteasy.auth.oauth.OAuthConsumer consumer) throws Exception {

        String[] scopes = consumer.getScopes();

        // build some info for verification
        net.oauth.OAuthConsumer _consumer = new net.oauth.OAuthConsumer(null, consumer.getKey(), consumer.getSecret(), null);
        OAuthAccessor accessor = new OAuthAccessor(_consumer);
        // validate the message
        validator.validateMessage(message, accessor, null);
    }
    //------------------------------------------------------------------------------

    /**
     * OAuth authentication context
     * Called from org.jboss.resteasy.auth.oauth.OAuthFilter
     *
     * @param request
     * @param consumer
     * @param accessToken
     * @return
     */
    @Override
    protected HttpServletRequest createSecurityContext(HttpServletRequest request, org.jboss.resteasy.auth.oauth.OAuthConsumer consumer, OAuthToken accessToken) {

        Set<String> roles = getProvider().convertPermissionsToRoles(accessToken.getScopes());
        UserEntity entity = loginManager.getUserOfAccessTokenEntity(accessToken.getToken());

        return new WebgephiHttpServletRequestWrapper(request, entity.getUsername(), roles, OAUTH_AUTH_METHOD);
    }

    /**
     * Basic authentication context
     *
     * @param request
     * @param username
     * @return
     */
    private HttpServletRequest createBasicContext(HttpServletRequest request, String username, Set<String> roles) {
        return new WebgephiHttpServletRequestWrapper(request, username, roles, HttpServletRequest.BASIC_AUTH);
    }

    public Set<String> loginAndGetRoles(String username, String password) {
        if (authenticationService.isLoggedIn()) {
            if (!authenticationService.getUser().getLoginName().equals(username)) {
                authenticationService.logout();
                authenticationService.login(username, password);
            }
        } else {
            authenticationService.login(username, password);
        }

        if (!authenticationService.isLoggedIn()) {
            return null;
        }

        List<Role> picketlinkRoles = authenticationService.getRoles();

        Set<String> roles = new HashSet<String>();
        for (org.jboss.errai.security.shared.Role r : picketlinkRoles) {
            cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role role = cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role.valueOf(r.getName());
            if (role == null) {
                log.warn("Such role does not exists: " + r.getName());
            } else {
                roles.add(role.name());
            }
        }
        return roles;
    }
}