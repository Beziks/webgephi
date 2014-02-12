package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.auth;

import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.WelcomePage;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Alert;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Login;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Logout;
import org.jboss.errai.bus.client.api.BusErrorCallback;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.client.local.Identity;
import org.jboss.errai.security.shared.User;
import org.jboss.errai.ui.nav.client.local.TransitionTo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 21.1.14
 * Time: 0:02
 */
@ApplicationScoped
public class AuthController {
    @Inject
    @Alert
    private javax.enterprise.event.Event<String> messageEvent;

    @Inject
    private TransitionTo<WelcomePage> welcomePage;

    @Inject
    private TransitionTo<LoginForm> loginForm;

    private void tryLogin(@Observes @Login Identity identity) {
        // messageEvent.fire("try login: " + identity.getUsername() + "/" + identity.getPassword());
        identity.login(
                new RemoteCallback<User>() {
                    @Override
                    public void callback(User user) {
                        // messageEvent.fire("Login success: " + user.getLoginName());
                    }
                },
                new BusErrorCallback() {
                    @Override
                    public boolean error(Message message, Throwable throwable) {
                        messageEvent.fire("Invalid credentials, try again...");
                        loginForm.go();
                        return false;
                    }
                }
        );
    }

    private void tryLogout(@Observes @Logout Identity identity) {
        identity.logout();
        welcomePage.go();
    }
}
