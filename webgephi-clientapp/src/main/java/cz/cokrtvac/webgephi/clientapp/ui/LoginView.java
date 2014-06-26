package cz.cokrtvac.webgephi.clientapp.ui;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import cz.cokrtvac.webgephi.client.WebgephiAuthenticator;
import cz.cokrtvac.webgephi.client.WebgephiAuthenticatorException;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@CDIView("login")
@SuppressWarnings("serial")
public class LoginView extends VerticalLayout implements View, Page.BrowserWindowResizeListener {
    @Inject
    private Logger log;

    @Inject
    private UserSession userSession;

    private final String[] SCOPES = new String[]{"PROFILE_READ", "GRAPHS_READ", "GRAPHS_WRITE"};

    @PostConstruct
    private void init() {
        userSession.setAuthenticator(
                new WebgephiAuthenticator(
                        userSession.getOAuthConsumerKey(), userSession.getOAuthConsumerSecret(),
                        userSession.getServerUrl(),
                        userSession.getBaseUrl()
                )
        );
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();

        // Show messages of failed logins
        if (userSession.getAuthFailedMessage() != null) {
            Notification.show("Authorization failed", userSession.getAuthFailedMessage(), Notification.Type.ERROR_MESSAGE);
        }
        if (userSession.getAuthDeniedMessage() != null) {
            Notification.show("Access denied", userSession.getAuthDeniedMessage(), Notification.Type.WARNING_MESSAGE);
        }
        userSession.setAuthDeniedMessage(null);
        userSession.setAuthFailedMessage(null);

        // Get request token and show link to login
        try {
            String url = userSession.getAuthenticator().authorize(SCOPES);

            VerticalLayout vl = new VerticalLayout();

            Label label = new Label("You have to login on webgephi server first");
            vl.addComponent(label);
            label.setStyleName("loginLabel");

            Link link = new Link("Login using OAuth", new ExternalResource(url));
            vl.addComponent(link);
            link.setStyleName("loginLink");

            Label info = new Label("(This application is only a graphic interface and is fully dependent on <a href='" + userSession.getServerUrl() + "'>" + userSession.getServerUrl() + "</a>. You must have an account there.)");
            info.setContentMode(ContentMode.HTML);
            vl.addComponent(info);
            info.setStyleName("infoLabel");

            addComponent(vl);

            setComponentAlignment(vl, Alignment.MIDDLE_CENTER);
            vl.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
            vl.setComponentAlignment(link, Alignment.MIDDLE_CENTER);
        } catch (WebgephiAuthenticatorException e) {
            log.error(e.getMessage(), e);
            Notification.show("Login failed", e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }

        updateSize(Page.getCurrent().getBrowserWindowHeight());
        Page.getCurrent().addBrowserWindowResizeListener(this);
    }

    @Override
    public void browserWindowResized(Page.BrowserWindowResizeEvent event) {
        updateSize(event.getHeight());
    }

    private void updateSize(int height) {
        int h = height - 70;
        setHeight(h, Unit.PIXELS);
    }
}
