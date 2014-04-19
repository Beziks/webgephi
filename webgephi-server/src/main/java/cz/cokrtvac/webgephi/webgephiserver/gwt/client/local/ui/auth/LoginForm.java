package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.auth;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.security.shared.LoginPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.enterprise.context.ApplicationScoped;

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
