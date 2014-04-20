package cz.cokrtvac.webgephi.clientapp.ui.template;

import com.vaadin.cdi.UIScoped;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;

import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 14:56
 */
@UIScoped
public class FooterWidget extends CustomComponent {
    @Inject
    private UserSession userSession;

    public FooterWidget() {
    }

    public void init() {
        Label label = new Label("Diploma project FIT ČVUT. For more info see <a href='" + userSession.getServerUrl() + "'>Webgephi server</a>. Based on <a href='http://gephi.org/'>Gephi</a>");
        label.setContentMode(ContentMode.HTML);
        label.setStyleName("label");

        VerticalLayout layout = new VerticalLayout(label);
        layout.setHeight(24, Unit.PIXELS);
        layout.setStyleName("page_footer");
        setCompositionRoot(layout);
    }


}
