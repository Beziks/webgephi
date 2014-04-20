package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.account;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.FormWidget;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.WelcomePage;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Alert;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Login;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.UserService;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.User;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.VoidCallback;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.security.client.local.Identity;
import org.jboss.errai.ui.nav.client.local.TransitionTo;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Set;

@Templated("#root")
public class UserProfileWidget extends FormWidget {
    @Inject
    @Alert
    private javax.enterprise.event.Event<String> messageEvent;

    @Inject
    @Login
    private javax.enterprise.event.Event<Identity> loginEvent;

    @Inject
    private TransitionTo<WelcomePage> welcomePage;

    @Inject
    private Identity identity;

    @Inject
    private Caller<UserService> userServiceCaller;

    @Inject
    private Validator validator;

    @Inject
    @AutoBound
    private DataBinder<User> user;

    @Inject
    @Bound
    @DataField
    private TextBox username;

    @DataField
    private Element usernameError = DOM.createDiv();

    @Inject
    @Bound
    @DataField
    private PasswordTextBox password;

    @DataField
    private Element passwordError = DOM.createDiv();

    @Inject
    @Bound
    @DataField
    private TextBox email;

    @DataField
    private Element emailError = DOM.createDiv();

    @Inject
    @Bound
    @DataField
    private TextBox firstName;

    @DataField
    private Element firstNameError = DOM.createDiv();

    @Inject
    @Bound
    @DataField
    private TextBox lastName;

    @DataField
    private Element lastNameError = DOM.createDiv();

    private HashMap<String, Element> errors = new HashMap<String, Element>();

    @PostConstruct
    private void init() {
        errors.put("username", usernameError);
        errors.put("password", passwordError);
        errors.put("firstName", firstNameError);
        errors.put("lastName", lastNameError);
        errors.put("email", emailError);

        for (Element e : errors.values()) {
            hideError(e);
        }

        user.addPropertyChangeHandler(new PropertyChangeHandler<Object>() {
            @Override
            public void onPropertyChange(PropertyChangeEvent<Object> objectPropertyChangeEvent) {
                Set<ConstraintViolation<Object>> errs = validator.validateProperty(objectPropertyChangeEvent.getSource(), objectPropertyChangeEvent.getPropertyName());

                Element error = errors.get(objectPropertyChangeEvent.getPropertyName());
                String message = "";

                for (ConstraintViolation<Object> v : errs) {
                    message += v.getMessage() + "<br />";
                }

                if (!message.isEmpty()) {
                    message = message.substring(0, message.length() - 6);
                    showError(error, message);
                } else {
                    hideError(error);
                }
            }
        });
    }

    private void hideError(Element e) {
        e.getStyle().setDisplay(Style.Display.NONE);
    }

    private void showError(Element e, String message) {
        e.setInnerHTML(message);
        e.getStyle().setDisplay(Style.Display.BLOCK);
    }

    // PUBLIC staff =======================================================
    public Button getSubmitButton() {
        return submitButton;
    }

    public DataBinder<User> getUser() {
        return user;
    }

    public TextBox getUsername() {
        return username;
    }

    private Runnable onSubmit = new Runnable() {
        @Override
        public void run() {
            if (validator.validate(user.getModel()).isEmpty()) {
                try {
                    userServiceCaller.call(successCallback, errorCallback).create(user.getModel());
                } catch (ValidationException e) {
                    messageEvent.fire("User could not be saved: " + e.getMessage());
                }
            } else {
                messageEvent.fire("Invalid input values. Fix it first...");
            }
        }
    };

    private ErrorCallback<Object> errorCallback = new ErrorCallback<Object>() {
        @Override
        public boolean error(Object message, Throwable throwable) {
            messageEvent.fire(throwable.getMessage());
            return false;
        }
    };

    private VoidCallback successCallback = new VoidCallback() {
        @Override
        public void callback(Void response) {
            identity.setUsername(user.getModel().getUsername());
            identity.setPassword(user.getModel().getPassword());
            loginEvent.fire(identity);
            welcomePage.go();
        }
    };

    public void setOnSubmit(Runnable onSubmit) {
        this.onSubmit = onSubmit;
    }


    @Override
    protected void onSubmit() {
        onSubmit.run();
    }
}
