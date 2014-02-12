package cz.cokrtvac.webgephi.webgephiserver.gwt.server;

import cz.cokrtvac.webgephi.webgephiserver.core.ejb.UserDAO;
import cz.cokrtvac.webgephi.webgephiserver.core.util.security_annotation.Secure;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.UserService;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthConsumerEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.User;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.AuthenticationService;

import javax.inject.Inject;
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
        // Because of lazy loading and to save traffic
        e.getUser().setGraphs(null);
        e.setAccessTokens(null);
        e.setRequestTokens(null);
        return e;
    }

    @Secure(owner = "#{arg0.user.username}")
    @Override
    public OAuthConsumerEntity createOrUpdate(OAuthConsumerEntity clientAppEntity) throws ValidationException {
        OAuthConsumerEntity e = userDAO.createOrUpdate(clientAppEntity, authenticationService.getUser().getLoginName());
        // Because of lazy loading and to save traffic
        e.getUser().setGraphs(null);
        e.getUser().setAccessTokens(null);
        e.setAccessTokens(null);
        e.setRequestTokens(null);
        return e;
    }
}
