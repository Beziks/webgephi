package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.auth;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.FormWidget;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.WelcomePage;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Login;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Logout;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.security.client.local.Identity;
import org.jboss.errai.security.shared.LoggedInEvent;
import org.jboss.errai.security.shared.LoggedOutEvent;
import org.jboss.errai.security.shared.User;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageShown;
import org.jboss.errai.ui.nav.client.local.TransitionTo;
import org.jboss.errai.ui.nav.client.local.api.LoginPage;
import org.jboss.errai.ui.shared.api.annotations.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@Page(role = LoginPage.class)
@Templated("#root")
@ApplicationScoped
public class LoginForm /*extends FormWidget*/ extends Composite {
   /* @Inject
    @Login
    private javax.enterprise.event.Event<Identity> loginEvent;

    @Inject
    @Logout
    private javax.enterprise.event.Event<Identity> logoutEvent;

    @Inject
    @Model
    Identity identity;

    @Inject
    @Bound
    @DataField
    private TextBox username;

    @DataField
    private Element form = DOM.createDiv();

    @Inject
    @Bound
    @DataField
    private PasswordTextBox password;

    @Inject
    @DataField
    private Anchor logout;

    // Login button clicked
    @Override
    protected void onSubmit() {
        loginEvent.fire(identity);
    }

    @EventHandler("logout")
    private void logoutClicked(ClickEvent event) {
        logoutEvent.fire(identity);
    }

    @AfterInitialization
    private void init() {
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
            }
        });
    }

    private void onLoggedIn(@Observes LoggedInEvent loggedInEvent) {
        form.getStyle().setDisplay(Style.Display.NONE);
        logout.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
    }

    private void onLoggedOut(@Observes LoggedOutEvent loggedOutEvent) {
        form.getStyle().setDisplay(Style.Display.BLOCK);
        logout.getElement().getStyle().setDisplay(Style.Display.NONE);
    }  */
}
