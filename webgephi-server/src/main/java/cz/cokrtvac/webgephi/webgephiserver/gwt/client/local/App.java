package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.NavBar;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.WelcomePage;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.message.Message;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Alert;
import org.jboss.errai.security.client.local.Identity;
import org.jboss.errai.ui.nav.client.local.Navigation;
import org.jboss.errai.ui.nav.client.local.TransitionTo;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.Validator;

/**
 *
 */
@Templated("#root")
@ApplicationScoped
public class App extends Composite {
    @Inject
    TransitionTo<WelcomePage> welcomePage;

    @Inject
    private Validator validator;

    @Inject
    private Navigation navigation;

    @Inject
    @DataField
    private NavBar navbar;

    @Inject
    @DataField
    private SimplePanel content;

    @Inject
    @DataField
    private Message message;

    @Inject
    private Identity identity;

    @PostConstruct
    public void clientMain() {
        content.add(navigation.getContentPanel());
        RootPanel.get().add(this);
        message.hide();
    }

    // TODO delete in production
   /* @AfterInitialization
    private void startTestEnvironment() {

        identity.setUsername("admin");
        identity.setPassword("password");
        identity.login(null, null);
    }*/

    private void showMessage(@Observes @Alert String text) {
        if (text == null) {
            message.hide();
        }
        message.show(text);
    }
}
