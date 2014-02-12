package cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared;

import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.docs.rest.RestDescription;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthRequestTokenEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.UserEntity;
import org.jboss.errai.bus.server.annotations.Remote;

import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 26.1.14
 * Time: 12:46
 */
@Remote
public interface DocsService {
    public List<RestDescription> getRestDescriptions();
}
