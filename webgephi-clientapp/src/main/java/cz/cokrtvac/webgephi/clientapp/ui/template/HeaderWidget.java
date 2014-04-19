package cz.cokrtvac.webgephi.clientapp.ui.template;

import com.vaadin.cdi.UIScoped;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import cz.cokrtvac.webgephi.api.model.user.UserXml;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 14:56
 */
@UIScoped
public class HeaderWidget extends CustomComponent {
    @Inject
    private Logger log;

    @Inject
    private UserSession userSession;

    private Button logoutButton;

    @PostConstruct
    public void init() {
        Label h1 = new Label("Webgephi client application");
        h1.setStyleName("h1");

        CssLayout center_box = new CssLayout(h1);
        center_box.setStyleName("center_box");

        CssLayout headerLayout = new CssLayout(center_box);
        headerLayout.setStyleName("page_header");

        logoutButton = new Button("Logout");
        logoutButton.setVisible(false);
        logoutButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                VaadinService.getCurrentRequest().getWrappedSession().invalidate();
                getUI().getPage().setLocation("");
            }
        });
        logoutButton.setStyleName("logoutButton");

        try {
            if(userSession.isLoggedIn()) {
                UserXml loggedUser = userSession.getWebgephiClient().getLoggedUser();
                if (loggedUser != null) {
                    onLogin(loggedUser);
                }
            }
        } catch (Exception e) {
            log.error("Logged user cannot be obtained: " + e.getMessage(), e);
        }

        headerLayout.addComponent(logoutButton);

        setCompositionRoot(headerLayout);
    }

    public void onLogin(@Observes UserXml user){
        logoutButton.setCaption("Logout (" + user.getUsername() + ")");
        logoutButton.setVisible(true);
    }
}
