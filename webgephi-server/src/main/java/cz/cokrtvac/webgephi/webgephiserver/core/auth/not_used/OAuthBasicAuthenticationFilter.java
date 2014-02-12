package cz.cokrtvac.webgephi.webgephiserver.core.auth.not_used;

import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import org.apache.commons.codec.binary.Base64;
import org.jboss.resteasy.auth.oauth.OAuthFilter;
import org.jboss.resteasy.auth.oauth.OAuthToken;
import org.jboss.resteasy.auth.oauth.OAuthUtils;
import org.picketlink.Identity;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.User;
import org.picketlink.idm.query.IdentityQuery;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

public class OAuthBasicAuthenticationFilter extends OAuthFilter {

    @Inject
    private Logger log;

    @Inject
    private Identity identity;

    @Inject
    private DefaultLoginCredentials credentials;

    @Inject
    private IdentityManager identityManager;

    public OAuthBasicAuthenticationFilter() {
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
            super._doFilter(request, response, filterChain);
        } else if (header != null && header.startsWith("Basic")) {
            log.info("Authorization using Basic");
            String base64Value = header.substring(6);
            Base64 base64 = new Base64();
            String decoded = new String(base64.decode(base64Value.getBytes()));
            String[] pair = decoded.split(":");
            String username = pair[0];
            String password = pair[1];

            if (!login(username, password)) {
                log.info("Authentication failed, username=" + username);
                response.sendError(Response.Status.UNAUTHORIZED.getStatusCode(), "Unauthorized. Authentication failed for " + username + ". You can log in using OAuth v1 or Basic.");
                return;
            }

            request = createSecurityContext(request, username);

            filterChain.doFilter(request, response);
        } else {
            response.addHeader("WWW-Authenticate", "Basic realm=\"default\"");
            response.sendError(Response.Status.UNAUTHORIZED.getStatusCode(), "Unauthorized. You can log in using OAuth v1 or Basic.");
        }
    }

    /**
     * OAuth authentication context
     * Called from org.jboss.resteasy.auth.oauth.OAuthFilter
     *
     * @param request
     * @param consumer
     * @param accessToken
     * @return
     */
    protected HttpServletRequest createSecurityContext(HttpServletRequest request, org.jboss.resteasy.auth.oauth.OAuthConsumer consumer, OAuthToken accessToken) {
        final Principal principal = new SimplePrincipal(consumer.getKey());
        final Set<String> roles = getProvider().convertPermissionsToRoles(accessToken.getPermissions());
        return new HttpServletRequestWrapper(request) {
            @Override
            public Principal getUserPrincipal() {
                return principal;
            }

            @Override
            public boolean isUserInRole(String role) {
                return roles.contains(role);
            }

            @Override
            public String getAuthType() {
                return OAUTH_AUTH_METHOD;
            }
        };
    }

    /**
     * Basic authentication context
     *
     * @param request
     * @param username
     * @return
     */
    private HttpServletRequest createSecurityContext(HttpServletRequest request, String username) {
        final Principal principal = new SimplePrincipal(username);

        final Set<String> roles = getRoles(username);

        return new HttpServletRequestWrapper(request) {
            @Override
            public Principal getUserPrincipal() {
                return principal;
            }

            @Override
            public boolean isUserInRole(String role) {
                return roles.contains(role);
            }

            @Override
            public String getAuthType() {
                return BASIC_AUTH;
            }
        };
    }

    private static class SimplePrincipal implements Principal {
        private String name;

        public SimplePrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public boolean login(String username, String password) {
        if(identity.isLoggedIn()){
            identity.logout();
        }
        credentials.setUserId(username);
        credentials.setPassword(password);
        Identity.AuthenticationResult result = identity.login();
        if (result.equals(Identity.AuthenticationResult.SUCCESS)) {
            log.info("Basic login successful: " + result);
            return true;
        }

        log.info("Basic login failed: " + result);
        return false;
    }

    public Set<String> getRoles(String username) {
        User u = identityManager.getUser(username);

        IdentityQuery<Role> query = identityManager.createIdentityQuery(Role.class);
        query.setParameter(Role.ROLE_OF, u);

        log.info("Roles of user " + u.getLoginName() + "...");
        Set<String> result = new HashSet<String>();
        for (Role r : query.getResultList()) {
            log.info("role: " + r.getName());
            result.add(r.getName());
        }

        return result;
    }
}