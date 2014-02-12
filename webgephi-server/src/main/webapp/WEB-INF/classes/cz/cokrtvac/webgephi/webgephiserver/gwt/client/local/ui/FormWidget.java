package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;

import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 23.1.14
 * Time: 15:09
 * <p/>
  * Template has to contain {@code <form data-field="submitForm" />} and {@code <button  data-field="submitButton" />}
 */
public abstract class FormWidget extends Composite {
    @Inject
    @DataField
    protected Button submitButton;

    @DataField
    protected Element submitForm = DOM.createForm();

    @EventHandler("submitForm")
    protected void enterPressed(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            onSubmit();
        }
    }

    @EventHandler("submitButton")
    protected void loginClicked(ClickEvent event) {
        onSubmit();
    }

    protected abstract void onSubmit();
}
