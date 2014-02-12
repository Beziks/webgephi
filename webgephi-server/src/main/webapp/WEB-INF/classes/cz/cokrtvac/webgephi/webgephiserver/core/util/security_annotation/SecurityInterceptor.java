package cz.cokrtvac.webgephi.webgephiserver.core.util.security_annotation;

import cz.cokrtvac.webgephi.webgephiserver.core.util.security_annotation.expression_resolver.ExpressionResolver;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.NoSuchRoleException;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role;
import org.apache.catalina.realm.GenericPrincipal;
import org.jboss.errai.security.shared.AuthenticationService;
import org.picketlink.idm.IdentityManager;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 27.1.14
 * Time: 21:23
 */
@Interceptor
@Secure({})
public class SecurityInterceptor {
    private static final String NOT_LOGGED_MESSAGE = "You are NOT logged in. You don't have permission to access this method (resource)";
    private static final String NO_PERMISSION_MESSAGE = "You don't have permission to access this method (resource)";

    @Inject
    private Logger log;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private IdentityManager identityManager;

    @AroundInvoke
    public Object invoke(InvocationContext ctx) throws Exception {
        Secure secure = getSecureAnnotation(ctx.getMethod());
        log.debug("Checking secure annotation " + secure + " on method " + ctx.getMethod().getName());

        UserInfo loggedUser = getLoggedUser(ctx);
        log.info("Logged user: " + loggedUser);

        if (loggedUser == null) {
            throw new SecurityException(NOT_LOGGED_MESSAGE);
        }

        if (secure.owner().isEmpty()) {
            boolean b = hasPermission(loggedUser, secure.value(), null);
            log.debug("Roles check (without owner condition): " + b);
            if (!b) {
                throw new SecurityException(NO_PERMISSION_MESSAGE);
            }

            // To skip initialization of ExpressionResolver
            if (secure.condition().isEmpty()) {
                return ctx.proceed();
            }
        }

        ExpressionResolver resolver = new ExpressionResolver();
        for (int i = 0; i < ctx.getParameters().length; i++) {
            Object parameter = ctx.getParameters()[i];
            resolver.addVariable(getArgName(i), parameter);
        }

        if (!secure.owner().isEmpty()) {
            String ownerUsername = resolver.resolve(secure.owner(), String.class);
            log.debug("Roles check (with owner condition). Owner=" + ownerUsername);
            boolean b = hasPermission(loggedUser, secure.value(), ownerUsername);
            log.debug("Roles check (with owner condition). Owner=" + ownerUsername + ". Result: " + b);

            if (!b) {
                throw new SecurityException(NO_PERMISSION_MESSAGE);
            }
        }


        if (!secure.condition().isEmpty()) {
            Boolean res = resolver.resolve(secure.condition(), Boolean.class);
            log.debug("Additional check: " + res);
            if (res == null || !res) {
                throw new SecurityException(NO_PERMISSION_MESSAGE);
            }
        }

        log.info("All security checks are OK, continue with method: " + ctx.getMethod().getName());
        return ctx.proceed();
    }

    private Secure getSecureAnnotation(Method m) {
        for (Annotation a : m.getAnnotations()) {
            if (a instanceof Secure) {
                return (Secure) a;
            }
        }
        for (Annotation a : m.getDeclaringClass().getAnnotations()) {
            if (a instanceof Secure) {
                return (Secure) a;
            }
        }

        throw new RuntimeException("@Secure not found on method " + m.getName() + " or its class " + m.getClass().getName());
    }

    private boolean hasPermission(UserInfo userInfo, Role[] requiredRoles, String resourceOwner) {
        for (Role r : userInfo.roles) {
            if (r.providesPermission(userInfo.username, resourceOwner, requiredRoles)) {
                return true;
            }
        }
        return false;
    }

    private UserInfo getLoggedUser(InvocationContext ctx) {
        UserInfo user = getServletUser(ctx);
        if (user != null) {
            log.info("User is set in servlet");
            user.roles.add(Role.ANY);
            return user;
        }

        user = getPicketlinkUser(ctx);
        if (user != null) {
            log.info("User is set in picketlink");
            user.roles.add(Role.ANY);
            return user;
        }

        log.info("User is not set at all (he is not logged in, probably)");
        return null;
    }

    private UserInfo getServletUser(InvocationContext ctx) {
        HttpServletRequest request = null;
        for (int i = 0; i < ctx.getParameters().length; i++) {
            Object parameter = ctx.getParameters()[i];
            if (parameter instanceof HttpServletRequest) {
                request = (HttpServletRequest) parameter;
                break;
            }
        }
        if (request == null) {
            log.debug("No HttpRequest found in method params.");
            return null;
        }

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            log.debug("Principal not found in request.");
            return null;
        }

        if (!(principal instanceof GenericPrincipal)) {
            log.debug("Principal is wrong type.");
            return null;
        }

        GenericPrincipal genericPrincipal = (GenericPrincipal) principal;
        UserInfo userInfo = new UserInfo();
        userInfo.username = principal.getName();
        if (userInfo.username == null) {
            log.debug("Username not set.");
            return null;
        }
        if (genericPrincipal.getRoles() == null) {
            log.debug("Roles not set.");
            return null;
        }

        try {
            userInfo.roles = Role.parse(genericPrincipal.getRoles());
        } catch (NoSuchRoleException e) {
            log.warn("Roles could not be parsed", e);
            throw new SecurityException("Invalid roles defined in request");
        }

        return userInfo;
    }

    private UserInfo getPicketlinkUser(InvocationContext ctx) {
        if (!authenticationService.isLoggedIn()) {
            log.debug("Picketlink user is not logged in");
            return null;
        }

        String username = authenticationService.getUser().getLoginName();
        List<org.jboss.errai.security.shared.Role> picketlinkRoles = authenticationService.getRoles();

        Set<Role> roles = new HashSet<Role>();
        for (org.jboss.errai.security.shared.Role r : picketlinkRoles) {
            Role role = Role.valueOf(r.getName());
            if (role == null) {
                log.warn("Such role does not exists: " + r.getName());
            } else {
                roles.add(role);
            }

        }

        UserInfo userInfo = new UserInfo();
        userInfo.username = username;
        userInfo.roles = roles;
        return userInfo;
    }

    private String getArgName(int index) {
        return "arg" + index;
    }

    private static class UserInfo {
        public String username;
        public Set<Role> roles;

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            for (Role role : roles) {
                b.append(role.name() + " ");
            }

            return "UserInfo{" +
                    "username='" + username + '\'' +
                    ", roles=" + b.toString() +
                    '}';
        }
    }
}
