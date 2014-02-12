package cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared;

import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthRequestTokenEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.UserEntity;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 26.1.14
 * Time: 12:46
 */
@Remote
public interface OAuthService {
    public OAuthRequestTokenEntity getRequestToken(String requestToken);
    public OAuthRequestTokenEntity authorizeRequestToken(String consumerKey, OAuthRequestTokenEntity requestTokenEntity);
    public UserEntity getUserWithAuthorizedAccessTokens();
    public boolean deleteAccessTokens(String accessTokenEntity);
}
