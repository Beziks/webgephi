package cz.cokrtvac.webgephi.clientapp.ui.template;

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 14:56
 */
@UIScoped
public class FooterWidget extends CustomComponent {
    public FooterWidget() {

        Label label = new Label("copyleft");
        label.setStyleName("label");

        VerticalLayout layout = new VerticalLayout(label);
        layout.setHeight(24, Unit.PIXELS);
        layout.setStyleName("page_footer");
        setCompositionRoot(layout);
    }
}
