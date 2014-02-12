package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.oauth_authorization;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.FormWidget;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.WelcomePage;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Alert;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.OAuthService;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.UserService;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthRequestTokenEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role;
import net.oauth.OAuth;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.security.shared.RequireAuthentication;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageShown;
import org.jboss.errai.ui.nav.client.local.TransitionTo;
import org.jboss.errai.ui.shared.api.annotations.*;
import org.slf4j.Logger;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Templated("#root")
@Page(path = "authorizeApp")
@RequireAuthentication
public class AuthorizeConsumerPage extends FormWidget {
    @Inject
    private Logger log;

    @Inject
    @Alert
    private javax.enterprise.event.Event<String> messageEvent;

    @Inject
    private TransitionTo<WelcomePage> welcomePage;

    @Inject
    private Instance<ScopeWidget> scopeWidgets;

    @Inject
    private Caller<UserService> userServiceCaller;

    @Inject
    private Caller<OAuthService> oAuthServiceCaller;

    @Inject
    @DataField
    private Button cancelButton;

    @Inject
    @AutoBound
    private DataBinder<OAuthRequestTokenEntity> requestToken;

    @DataField
    @Bound(property = "consumer.applicationName")
    private Element applicationName = DOM.createSpan();

    @DataField
    @Bound(property = "consumer.applicationName")
    private Element applicationName2 = DOM.createSpan();

    @Inject
    @DataField
    private FlowPanel scopes;

    @PageShown
    private void init() {
        String token = Window.Location.getParameter("oauth_token");
        if (token == null) {
            messageEvent.fire("Request token (oauth_token) is missing.");
            welcomePage.go();
            return;
        }

        oAuthServiceCaller.call(
                new RemoteCallback<OAuthRequestTokenEntity>() {
                    @Override
                    public void callback(OAuthRequestTokenEntity o) {
                        log.info("Request token loaded " + o.getToken() + " | " + o.getConsumer().getKey());
                        requestToken.setModel(o);
                        for (String s : o.getScopes()) {
                            Role r = Role.valueOf(s);
                            if (r != null) {
                                ScopeWidget w = scopeWidgets.get();
                                w.init(r);
                                scopes.add(w);
                            }
                        }
                    }
                },
                new ErrorCallback<Object>() {
                    @Override
                    public boolean error(Object o, Throwable throwable) {
                        if (!throwable.getMessage().contains("SecurityException")) {
                            messageEvent.fire("Request token is invalid");
                            log.error("Request token is invalid", throwable);
                            welcomePage.go();
                        }
                        return false;
                    }
                }
        ).getRequestToken(token);


    }


    @Override
    protected void onSubmit() {
        oAuthServiceCaller.call(
                new RemoteCallback<OAuthRequestTokenEntity>() {
                    @Override
                    public void callback(OAuthRequestTokenEntity o) {
                        messageEvent.fire("Request token authorized, redirecting back... ");
                        Window.Location.assign(o.getCallback() + "?" + OAuth.OAUTH_TOKEN + "=" + o.getToken() + "&" + OAuth.OAUTH_VERIFIER + "=" + o.getVerifier());
                    }
                },
                new ErrorCallback<Object>() {
                    @Override
                    public boolean error(Object o, Throwable throwable) {
                        messageEvent.fire("Authorization failed, redirecting back...");
                        failed(throwable.getMessage());
                        return false;
                    }
                }
        ).authorizeRequestToken(requestToken.getModel().getConsumer().getKey(), requestToken.getModel());
    }

    @EventHandler("cancelButton")
    protected void cancelClicked(ClickEvent event) {
        messageEvent.fire("Authorization denied, redirecting back...");
        Window.Location.assign(
                requestToken.getModel().getCallback()
                        + "?denied=" + URL.encodeQueryString("Access denied by user"));
    }

    private void failed(String message) {
        Window.Location.assign(requestToken.getModel().getCallback()
                + "?failed=" + URL.encodeQueryString(message));
    }
}
