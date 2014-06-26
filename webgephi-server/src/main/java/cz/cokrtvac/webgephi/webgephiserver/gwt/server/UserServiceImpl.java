package cz.cokrtvac.webgephi.webgephiserver.gwt.server;

import cz.cokrtvac.webgephi.webgephiserver.core.ejb.UserDAO;
import cz.cokrtvac.webgephi.webgephiserver.core.util.security_annotation.Secure;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.UserService;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.GraphEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthConsumerEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.User;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.AuthenticationService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Inject
    AuthenticationService authenticationService;

    @Inject
    private UserDAO userDAO;

    @Override
    public void create(User user) throws ValidationException {
        userDAO.createRegularUser(user);
    }

    @Secure(value = Role.USER, owner = "#{arg0.username}")
    @Override
    public void update(User user) throws ValidationException {
        userDAO.update(user);
    }

    @Secure
    @Override
    public User getCurrentUser() {
        if (!authenticationService.isLoggedIn()) {
            return null;
        }

        String username = authenticationService.getUser().getLoginName();
        User u = userDAO.get(username);
        return u;
    }

    @Override
    public String makeRandomString() {
        return UUID.randomUUID().toString();
    }

    @Secure
    @Override
    public OAuthConsumerEntity getClientAppEntity() throws ValidationException {
        OAuthConsumerEntity e = userDAO.getClientApp(authenticationService.getUser().getLoginName());
        if (e == null) {
            return null;
        }

        // Because of lazy loading and to save traffic
        e.setUser(null);
        e.setAccessTokens(null);
        e.setRequestTokens(null);
        return e;
    }

    @Override
    public OAuthConsumerEntity createOrUpdate(OAuthConsumerEntity clientAppEntity) throws ValidationException {
        if (clientAppEntity.getUser() != null && !clientAppEntity.getUser().getUsername().equals(authenticationService.getUser().getLoginName())) {
            throw new SecurityException("You dont have a permission to save this consumer app.");
        }

        OAuthConsumerEntity e = userDAO.createOrUpdate(clientAppEntity, authenticationService.getUser().getLoginName());
        e = getClientAppEntity();
        return e;
    }
}
