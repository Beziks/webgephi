package cz.cokrtvac.webgephi.webgephiserver.gwt.client.local.ui.message;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.inject.Inject;

@Templated("#root")
public class Message extends Composite {

    @Inject
    @DataField
    private InlineLabel label;

    @Inject
    @DataField
    private Button closeButton;

    @EventHandler("closeButton")
    private void onClose(ClickEvent e) {
        hide();
    }

    public void hide() {
        getElement().getStyle().setDisplay(Style.Display.NONE);
    }

    public void show(String message) {
        label.setText(message);
        getElement().getStyle().setDisplay(Style.Display.BLOCK);
    }
}
