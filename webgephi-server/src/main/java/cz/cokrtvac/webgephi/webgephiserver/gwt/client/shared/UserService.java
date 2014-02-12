package cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared;

import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthConsumerEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.User;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface UserService {
    // User profile
    public void create(User user) throws ValidationException;

    public void update(User user) throws ValidationException;

    public User getCurrentUser();

    // Client app
    public String makeRandomString();

    public OAuthConsumerEntity getClientAppEntity() throws ValidationException;

    public OAuthConsumerEntity createOrUpdate(OAuthConsumerEntity clientAppEntity) throws ValidationException;
}
