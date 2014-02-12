package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.account;

import com.google.gwt.user.client.ui.Composite;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Alert;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.UserService;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.User;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.VoidCallback;
import org.jboss.errai.security.shared.RequireAuthentication;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageShown;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.validation.Validator;

@Page(path = "editAccount")
@Templated("#root")
@RequireAuthentication
public class EditAccountPage extends Composite {
    @Inject
    Logger log;

    @Inject
    @Alert
    private javax.enterprise.event.Event<String> messageEvent;

    @Inject
    @DataField
    private UserProfileWidget profile;

    @Inject
    @DataField
    private AuthorizedConsumersWidget authorizedApps;

    @Inject
    private Validator validator;

    @Inject
    private Caller<UserService> userServiceCaller;

    @PageShown
    private void init() {
        log.info("init");
        profile.getSubmitButton().setTitle("Save");
        profile.getSubmitButton().setText("Save");
        profile.getUsername().setEnabled(false);
        profile.setOnSubmit(onSubmit);

        userServiceCaller.call(new RemoteCallback<User>() {
            @Override
            public void callback(User response) {
                if (response == null) {
                    messageEvent.fire("No user is logged");
                } else {
                    profile.getUser().setModel(response);
                    log.info("Set user: " + response);
                }
            }
        }).getCurrentUser();
    }

    private Runnable onSubmit = new Runnable() {
        @Override
        public void run() {
            if (validator.validate(profile.getUser().getModel()).isEmpty()) {
                try {
                    userServiceCaller.call(succesCallback, errorCallback).update(profile.getUser().getModel());
                } catch (ValidationException e) {
                    messageEvent.fire("User could not be saved: " + e.getMessage());
                }
            } else {
                messageEvent.fire("Invalid input values. Fix it first...");
            }
        }
    };

    private VoidCallback succesCallback = new VoidCallback() {
        @Override
        public void callback(Void response) {
            messageEvent.fire("Account was updated");
        }
    };

    private ErrorCallback<Object> errorCallback = new ErrorCallback<Object>() {
        @Override
        public boolean error(Object message, Throwable throwable) {
            messageEvent.fire(throwable.getMessage());
            return false;
        }
    };


}
