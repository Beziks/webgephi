package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.consumer;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.FormWidget;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.Alert;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.UserService;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthConsumerEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.PropertyChangeHandler;
import org.jboss.errai.security.client.local.Identity;
import org.jboss.errai.ui.shared.api.annotations.*;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 23.1.14
 * Time: 15:30
 */
@Templated("#root")
public class ConsumerApplicationWidget extends FormWidget {
    @Inject
    private Logger log;

    @Inject
    @Alert
    private javax.enterprise.event.Event<String> messageEvent;

    @Inject
    private Identity identity;

    @Inject
    private Validator validator;

    @Inject
    private Caller<UserService> userServiceCaller;

    @Inject
    @AutoBound
    private DataBinder<OAuthConsumerEntity> clientApp;

    @Inject
    @DataField
    @Bound
    private TextBox key;

    @DataField
    private Element keyError = DOM.createDiv();

    @Inject
    @DataField
    @Bound
    private TextBox secret;

    @DataField
    private Element secretError = DOM.createDiv();

    @Inject
    @DataField
    @Bound
    private TextBox applicationName;

    @DataField
    private Element applicationNameError = DOM.createDiv();

    @Inject
    @DataField
    @Bound
    private TextBox connectUri;

    @DataField
    private Element connectUriError = DOM.createDiv();

    @Inject
    @DataField
    private Button generateSecret;


    @Override
    protected void onSubmit() {
        if (validator.validate(clientApp.getModel()).isEmpty()) {
            try {
                log.info("Saving client app for " + identity.getUsername());
                userServiceCaller.call(succesCallback, errorCallback).createOrUpdate(clientApp.getModel());
            } catch (ValidationException e) {
                messageEvent.fire("User could not be saved: " + e.getMessage());
            }
        } else {
            messageEvent.fire("Invalid input values. Fix it first...");
        }
    }

    private RemoteCallback<OAuthConsumerEntity> succesCallback = new RemoteCallback<OAuthConsumerEntity>() {
        @Override
        public void callback(OAuthConsumerEntity response) {
            clientApp.setModel(response);
            messageEvent.fire("Client application was updated");
        }
    };


    private ErrorCallback<Object> errorCallback = new ErrorCallback<Object>() {
        @Override
        public boolean error(Object message, Throwable throwable) {
            messageEvent.fire(throwable.getMessage());
            return false;
        }
    };

    private HashMap<String, Element> errors = new HashMap<String, Element>();

    @PostConstruct
    private void init() {
        log.info("Initializing...");

        errors.put("key", keyError);
        errors.put("secret", secretError);
        errors.put("applicationName", applicationNameError);
        errors.put("connectUri", connectUriError);

        for (Element e : errors.values()) {
            hideError(e);
        }

        clientApp.addPropertyChangeHandler(new PropertyChangeHandler<Object>() {
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

        try {
            userServiceCaller.call(
                    new RemoteCallback<OAuthConsumerEntity>() {
                        @Override
                        public void callback(OAuthConsumerEntity response) {
                            if (response != null) {
                                log.info("Client app alredy exists, setting one");
                                clientApp.setModel(response);
                            } else {
                                log.info("Client app does not exist, new one will be created");
                            }
                        }
                    },
                    new ErrorCallback<Object>() {
                        @Override
                        public boolean error(Object message, Throwable throwable) {
                            log.warn(throwable.getMessage());
                            return false;
                        }
                    }
            ).getClientAppEntity();
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    private void hideError(Element e) {
        e.getStyle().setDisplay(Style.Display.NONE);
    }

    private void showError(Element e, String message) {
        e.setInnerHTML(message);
        e.getStyle().setDisplay(Style.Display.BLOCK);
    }

    @EventHandler("generateSecret")
    private void generateSecret(ClickEvent event) {
        log.info("Regenerating secret...");
        userServiceCaller.call(new RemoteCallback<String>() {
            @Override
            public void callback(String response) {
                clientApp.getModel().setSecret(response);
            }
        }).makeRandomString();
    }
}
