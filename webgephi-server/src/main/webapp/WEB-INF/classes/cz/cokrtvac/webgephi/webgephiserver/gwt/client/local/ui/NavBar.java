package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Login;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Logout;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.security.client.local.Identity;
import org.jboss.errai.security.shared.LoggedInEvent;
import org.jboss.errai.security.shared.LoggedOutEvent;
import org.jboss.errai.security.shared.User;
import org.jboss.errai.ui.nav.client.local.TransitionTo;
import org.jboss.errai.ui.shared.api.annotations.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
@Templated("#root")
public class NavBar extends FormWidget {
    @Inject
    @Login
    private javax.enterprise.event.Event<Identity> loginEvent;

    @Inject
    @Logout
    private javax.enterprise.event.Event<Identity> logoutEvent;

    @Inject
    TransitionTo<WelcomePage> welcomePage;

    @Inject
    @Model
    Identity identity;

    @Inject
    @Bound
    @DataField
    private TextBox username;

    @Inject
    @Bound
    @DataField
    private PasswordTextBox password;

    @Inject
    @DataField
    private Button logout;

    @DataField
    private Element form = DOM.createDiv();

    @DataField
    private Element userInfo = DOM.createDiv();

    @Override
    protected void onSubmit() {
        loginEvent.fire(identity);
    }

    @EventHandler("logout")
    private void logoutClicked(ClickEvent event) {
        logoutEvent.fire(identity);
    }

    @AfterInitialization
    public void init() {
        identity.getUser(new AsyncCallback<User>() {
            @Override
            public void onSuccess(User result) {
                if (result != null) {
                    onLoggedIn(null);
                } else {
                    onLoggedOut(null);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                onLoggedOut(null);
            }
        });
    }

    private void onLoggedIn(@Observes LoggedInEvent loggedInEvent) {
        form.getStyle().setDisplay(Style.Display.NONE);
        userInfo.getStyle().setDisplay(Style.Display.BLOCK);
    }

    private void onLoggedOut(@Observes LoggedOutEvent loggedOutEvent) {
        form.getStyle().setDisplay(Style.Display.BLOCK);
        userInfo.getStyle().setDisplay(Style.Display.NONE);
    }
}
