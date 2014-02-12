package cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 26.1.14
 * Time: 14:57
 */
public enum Role {
    PROFILE_READ {
        @Override
        public String description() {
            return "Can read profile data (username, email, ...) of the account. Password is excluded, of course.";
        }

        @Override
        public String restDescription() {
            return "GET method on " + getRestBaseUrl() + "users/{username}";
        }
    },
    PROFILE_WRITE {
        @Override
        public String description() {
            return "Can edit profile data (email, name ...) of the account, INCLUDING password.";
        }

        @Override
        public String restDescription() {
            return "PUT method on " + getRestBaseUrl() + "users/{username}";
        }
    },
    GRAPHS_READ {
        @Override
        public String description() {
            return "Can read graphs from your graph storage.";
        }

        @Override
        public String restDescription() {
            return "GET method on " + getRestBaseUrl() + "users/{username}/graphs and " + getRestBaseUrl() + "users/{username}/graphs/{graph_id}";
        }
    },
    GRAPHS_WRITE {
        @Override
        public String description() {
            return "Can insert new graphs to the graph storage and apply functions on existing ones";
        }

        @Override
        public String restDescription() {
            return "PUT (apply functon) method on " + getRestBaseUrl() + "users/{username}/graphs/{graph_id}. POST (insert new graph) method on " + getRestBaseUrl() + "users/{username}/graphs";
        }
    },
    ADMIN {
        @Override
        public String description() {
            return "Can do anything";
        }

        @Override
        public String restDescription() {
            return "Any method on " + getRestBaseUrl();
        }

        @Override
        public boolean providesPermission(String loggedUser, String ownerUser, Role... requiredRoles) {
            return true;
        }
    },
    USER {
        @Override
        public String description() {
            return "Marking role only. Means, that user logged in by himself (not by using oauth, for example)";
        }

        @Override
        public String restDescription() {
            return "";
        }
    },
    CLIENT_APP {
        @Override
        public String description() {
            return "Marking role only. Means, that user is in fact a client application (OAuth consumer).";
        }

        @Override
        public String restDescription() {
            return "";
        }
    },
    ANY {
        @Override
        public String description() {
            return "Everybody has this role. Used if no specific role is required (to be logged in is enough).";
        }

        @Override
        public String restDescription() {
            return "";
        }
    },;

    public abstract String description();

    public abstract String restDescription();

    public String getTitle() {
        String s = name().toLowerCase();
        String[] split = s.split("_");

        String res = "";
        for (int i = split.length - 1; i >= 0; i--) {
            res += split[i] + " ";
        }

        res = res.trim();
        res = res.substring(0, 1).toUpperCase() + res.substring(1);
        return res;
    }

    public boolean providesPermission(String loggedUser, String ownerUser, Role... requiredRoles) {
        return providesPermission(loggedUser, ownerUser, Arrays.asList(requiredRoles));
    }

    public boolean providesPermission(String loggedUser, String ownerUser, Collection<Role> requiredRoles) {
        if (loggedUser == null) {
            return false;
        }

        // Logged user has to be the same as owner of resource (if set)
        if (ownerUser != null && !loggedUser.equals(ownerUser)) {
            return false;
        }

        // If no role is required, user has permission by default
        if(requiredRoles == null || requiredRoles.isEmpty()){
            return true;
        }

        // This role is one of required roles
        if (requiredRoles.contains(this)) {
            return true;
        }

        return false;
    }

    // STATIC ===========================
    public static Set<Role> parse(String... roles) throws NoSuchRoleException {
        if (roles == null) {
            return new HashSet<Role>();
        }
        return parse(Arrays.asList(roles));
    }

    public static Set<Role> parse(Collection<String> roles) throws NoSuchRoleException {
        Set<Role> set = new HashSet<Role>();

        if (roles == null || roles.size() == 0) {
            return set;
        }

        for (String p : roles) {
            Role r = Role.valueOf(p);
            if (r == null) {
                throw new NoSuchRoleException(p);
            }
            set.add(r);
        }

        return set;
    }

    public static String getRestBaseUrl() {
        return "https://localhost:8888/webgephiserver/rest/";
    }
}
