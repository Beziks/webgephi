package cz.cokrtvac.webgephi.clientapp.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import cz.cokrtvac.webgephi.api.model.user.UserXml;
import cz.cokrtvac.webgephi.clientapp.model.UserSession;
import cz.cokrtvac.webgephi.clientapp.ui.template.FooterWidget;
import cz.cokrtvac.webgephi.clientapp.ui.template.HeaderWidget;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@Theme("mytheme")
@CDIUI
@SuppressWarnings("serial")
public class AppUI extends UI {
    @Inject
    private Logger log;

    @Inject
    private UserSession userSession;

    @Inject
    private HeaderWidget headerWidget;

    @Inject
    private FooterWidget footerWidget;

    @Inject
    private CDIViewProvider navigatorViewProvider;

    private Navigator navigator;

    @Inject
    private javax.enterprise.event.Event<UserXml> onLoginEvent;

    //@WebServlet(value = "/*", asyncSupported = true)
    //@VaadinServletConfiguration(productionMode = false, ui = AppUI.class, widgetset = "cz.cokrtvac.webgephi.clientapp.ui.AppWidgetSet")

    @WebServlet(value = "/*",
            asyncSupported = true,
            initParams = {@WebInitParam(
                    name = "session-timeout",
                    value = "60"
            ), @WebInitParam(
                    name = "UIProvider",
                    value = "com.vaadin.cdi.CDIUIProvider"
            )}
    )
    @VaadinServletConfiguration(
            productionMode = false,
            ui = AppUI.class,
            closeIdleSessions = true,
            widgetset = "cz.cokrtvac.webgephi.clientapp.ui.AppWidgetSet"
    )
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        CssLayout layout = new CssLayout();

        setContent(layout);

        layout.addComponent(headerWidget);

        Panel mainContent = new Panel();
        layout.addComponent(mainContent);
        layout.addComponent(footerWidget);

        // Init navigator
        navigator = new Navigator(this, mainContent);
        navigator.addProvider(navigatorViewProvider);

        if (!userSession.isLoggedIn()) {
            navigator.navigateTo("login");
            return;
        } else {
            try {
                onLoginEvent.fire(userSession.getWebgephiClient().getLoggedUser());
            } catch (Exception e) {
                log.error("Logged user cannot be obtained: " + e.getMessage(), e);
            }
        }
    }
}
