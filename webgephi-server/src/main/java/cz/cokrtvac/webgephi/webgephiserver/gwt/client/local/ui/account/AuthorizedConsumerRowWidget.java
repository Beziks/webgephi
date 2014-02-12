package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.account;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.FormWidget;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.oauth_authorization.ScopeWidget;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthAccessTokenEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.NoSuchRoleException;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.*;
import org.slf4j.Logger;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Set;

@Templated("AuthorizedConsumersWidget.html#rootConsumerRow")
public class AuthorizedConsumerRowWidget extends Composite {
    @Inject
    private Logger log;

    @Inject
    private Instance<ScopeWidget> scopeWidgets;

    @Inject
    @DataField
    private FlowPanel scopes;

    @Inject
    @AutoBound
    private DataBinder<OAuthAccessTokenEntity> accessTokenEntity;

    @Bound(property = "consumer.key")
    @DataField
    private Element consumerKey = DOM.createTD();

    @Bound(property = "consumer.connectUri")
    @DataField
    private Element consumerUrl = DOM.createTD();

    private Runnable onDelete;

    public void init(OAuthAccessTokenEntity accessTokenEntity, Runnable onDelete) {
        this.accessTokenEntity.setModel(accessTokenEntity);
        this.onDelete = onDelete;
        try {
            Set<Role> roles = Role.parse(accessTokenEntity.getScopes());
            for (Role r : roles) {
                ScopeWidget w = scopeWidgets.get();
                w.init(r);
                scopes.add(w);
            }
        } catch (NoSuchRoleException e) {
            log.error("Role cannot be parsed", e);
        }
    }


    @EventHandler("revokeButton")
    @SinkNative(Event.ONCLICK)
    protected void onSubmit(Event event) {
        onDelete.run();
    }
}
