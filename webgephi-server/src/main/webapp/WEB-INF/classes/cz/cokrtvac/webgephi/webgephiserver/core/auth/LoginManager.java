package cz.cokrtvac.webgephi.webgephiserver.core.auth;

import cz.cokrtvac.webgephi.webgephiserver.core.ejb.OAuthDAO;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.UserEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role;
import org.jboss.security.SimpleGroup;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Credentials;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.credential.UsernamePasswordCredentials;
import org.picketlink.idm.model.User;
import org.picketlink.idm.query.IdentityQuery;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.security.acl.Group;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 24.1.14
 * Time: 15:10
 */
@Stateless
public class LoginManager {
    @Inject
    private Logger log;

    @Inject
    private IdentityManager identityManager;

    @Inject
    private OAuthDAO oAuthDAO;

    public Credentials.Status validate(String username, String password) {
        UsernamePasswordCredentials picketLinkCredentials = new UsernamePasswordCredentials(username, new Password(password));
        identityManager.validateCredentials(picketLinkCredentials);
        log.info("Credentials validation status: " + picketLinkCredentials.getStatus());
        return picketLinkCredentials.getStatus();
    }

    // Not used
    public Group getRoleSets(String username) {
        User u = identityManager.getUser(username);

        IdentityQuery<org.picketlink.idm.model.Role> query = identityManager.createIdentityQuery(org.picketlink.idm.model.Role.class);
        query.setParameter(org.picketlink.idm.model.Role.ROLE_OF, u);

        List<org.picketlink.idm.model.Role> result = query.getResultList();

        log.info("Roles of user " + u.getLoginName() + "...");
        SimpleGroup g = new SimpleGroup("Roles");
        for (org.picketlink.idm.model.Role r : result) {
            log.info("role: " + r.getName());
            g.addMember(new SimpleGroup(r.getName()));
        }

        return g;
    }

    public Set<String> getUserRolesStrings(String username) {
        Set<Role> roles = getUserRoles(username);
        Set<String> out = new HashSet<String>();
        for (Role r : roles) {
            out.add(r.name());
        }
        return out;
    }

    public Set<Role> getUserRoles(String username) {
        User user = identityManager.getUser(username);
        IdentityQuery<org.picketlink.idm.model.Role> query = identityManager.createIdentityQuery(org.picketlink.idm.model.Role.class);
        query.setParameter(org.picketlink.idm.model.Role.ROLE_OF, user);

        log.info("Roles of user " + user.getLoginName() + "...");
        Set<Role> result = new HashSet<Role>();
        for (org.picketlink.idm.model.Role r : query.getResultList()) {
            log.info("role: " + r.getName());
            Role role = Role.valueOf(r.getName());
            if (role == null) {
                log.warn("Role " + r.getName() + "does not exist and will ber removed from user " + user.getLoginName());
                identityManager.revokeRole(user, r);
            } else {
                result.add(role);
            }
        }
        return result;
    }

    public UserEntity getUserOfAccessTokenEntity(String token) {
        return oAuthDAO.getAccessTokenEntity(token).getUser();
    }
}
