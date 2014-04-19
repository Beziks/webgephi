package cz.cokrtvac.webgephi.webgephiserver.core.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 8.3.14
 * Time: 21:13
 */
public class WebgephiHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private Principal principal;
    private Set<String> roles;
    private String authType;

    public WebgephiHttpServletRequestWrapper(HttpServletRequest request, String username, Set<String> roles, String authType) {
        super(request);
        this.principal = new PrincipalWithRoles(username, new HashSet<String>(roles));
        this.authType = authType;
    }

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
        return authType;
    }

    public static class PrincipalWithRoles implements Principal {
        private final Set<String> roles;
        private String name;

        public PrincipalWithRoles(String name, Set<String> roles) {
            this.name = name;
            this.roles = roles;
        }

        @Override
        public String getName() {
            return name;
        }

        public Set<String> getRoles() {
            return roles;
        }
    }
}
