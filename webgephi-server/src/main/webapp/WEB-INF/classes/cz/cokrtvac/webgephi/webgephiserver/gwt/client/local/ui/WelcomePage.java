package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.security.client.local.Identity;
import org.jboss.errai.security.shared.LoggedInEvent;
import org.jboss.errai.security.shared.LoggedOutEvent;
import org.jboss.errai.security.shared.User;
import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
@Templated("#root")
@Page(role = DefaultPage.class)
public class WelcomePage extends Composite {

    private static final String ANONYMOUS = "anonymous";
    private static final String TITLE = "Welcome to Webgephi SERVER";

    @Inject
    @DataField
    private InlineLabel title;

    @Inject
    private Identity identity;

    @AfterInitialization
    private void setupUserLabel() {
        identity.getUser(new AsyncCallback<User>() {
            @Override
            public void onSuccess(User user) {
                setTitleLabel(user);
            }

            @Override
            public void onFailure(Throwable caught) {
                title.setText(TITLE + ": ERROR");
            }
        });
    }

    private void setTitleLabel(User user) {
        title.setText(TITLE + ":" + (user != null ? user.getFullName() : ANONYMOUS));
    }

    private void onLoggedIn(@Observes LoggedInEvent loggedInEvent) {
        setTitleLabel(loggedInEvent.getUser());
    }

    private void onLoggedOut(@Observes LoggedOutEvent loggedOutEvent) {
        setTitleLabel(null);
    }
}
