package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.account;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Alert;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.OAuthService;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.UserService;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthAccessTokenEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.UserEntity;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 29.1.14
 * Time: 14:27
 */
@Templated("#root")
public class AuthorizedConsumersWidget extends Composite {
    @Inject
    Logger log;

    @Inject
    @Alert
    private javax.enterprise.event.Event<String> messageEvent;

    @Inject
    private Caller<UserService> userServiceCaller;

    @Inject
    private Caller<OAuthService> oAuthServiceCaller;

    @Inject
    private Instance<AuthorizedConsumerRowWidget> authorizedConsumerRowWidgets;

    @DataField
    private Element consumers = DOM.createTBody();

    private UserEntity userEntity;

    @PostConstruct
    private void init() {
        log.info("AuthorizedConsumersWidget INIT");
        consumers.setInnerHTML("");
        oAuthServiceCaller.call(new RemoteCallback<UserEntity>() {
            @Override
            public void callback(UserEntity response) {
                if (response == null) {
                    messageEvent.fire("User data could not be loaded");
                } else {
                    userEntity = response;
                    log.info("Set user: " + response);

                    for (OAuthAccessTokenEntity e : userEntity.getAccessTokens()) {
                        log.info("Adding access token " + e.getToken());
                        final OAuthAccessTokenEntity fe = e;
                        final AuthorizedConsumerRowWidget w = authorizedConsumerRowWidgets.get();
                        w.init(e, new Runnable() {
                            @Override
                            public void run() {
                                revoke(w.getElement(), fe);
                            }
                        });
                        consumers.appendChild(w.getElement());
                    }
                }
            }
        }).getUserWithAuthorizedAccessTokens();
    }

    private void revoke(final Element el, OAuthAccessTokenEntity accessTokenEntity) {
        log.info("Fire...");
        oAuthServiceCaller.call(new RemoteCallback<Boolean>() {
            @Override
            public void callback(Boolean o) {
                if (o) {
                    messageEvent.fire("Access revoked");
                } else {
                    messageEvent.fire("Access could not be revoked");
                }
                init();
            }
        }).deleteAccessTokens(accessTokenEntity.getToken());
    }

}
