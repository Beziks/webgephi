package cz.cokrtvac.webgephi.clientapp.model;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.client.*;
import cz.cokrtvac.webgephi.clientapp.model.data_source.GraphsQuery;
import cz.cokrtvac.webgephi.clientapp.ui.Selected;
import org.slf4j.Logger;
import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 21:03
 */
@SessionScoped
public class UserSession implements Serializable {
    public static final String PROP_OAUTH_CONSUMER_KEY = "OAUTH_CONSUMER_KEY";
    public static final String PROP_OAUTH_CONSUMER_SECRET = "OAUTH_CONSUMER_SECRET";
    public static final String PROP_WEBGEPHI_SERVER_URL = "WEBGEPHI_SERVER_URL";

    private static final String SERVER_URL_LOCAL = "https://webgephi.local:8443";
    private static final String SERVER_URL_PRODUCTION = "https://webgephi.cz";

    private String baseUrl;

    @Inject
    private Logger log;

    private WebgephiAuthenticator authenticator;
    private String authFailedMessage;
    private String authDeniedMessage;
    private WebgephiEntityClient webgephiClient;

    private LazyQueryContainer graphsLazyQueryContainer;

    private GraphDetailXml currentGraph;

    @Inject
    @Selected
    private Event<GraphDetailXml> graphSelectedEvent;

    public WebgephiAuthenticator getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(WebgephiAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void setVerifier(String verifier) {
        try {
            Token accessToken = authenticator.obtainAccessToken(verifier);
            afterLogin(accessToken);
            log.info("Authorization success");
        } catch (WebgephiAuthenticatorException e) {
            log.error("Request for access token failed", e);
        }
    }

    private void afterLogin(Token accessToken) {
        Token t = accessToken;
        webgephiClient = new CachingWebgephiEntityClient(new WebgephiEntityClientImpl(new WebgephiOAuthClient(getServerUrl() + "/rest/v1", t)));

        BeanQueryFactory<GraphsQuery> queryFactory = new BeanQueryFactory<GraphsQuery>(GraphsQuery.class);

        Map<String, Object> queryConfiguration = new HashMap<String, Object>();
        queryConfiguration.put("userSession", this);
        queryFactory.setQueryConfiguration(queryConfiguration);
        graphsLazyQueryContainer = new LazyQueryContainer(queryFactory, "id", 50, false);
        graphsLazyQueryContainer.addContainerProperty("id", Long.class, 0, true, false);
        graphsLazyQueryContainer.addContainerProperty("name", String.class, "name", true, false);
        graphsLazyQueryContainer.addContainerProperty("created", Date.class, null, true, false);
    }

    @Produces
    public LazyQueryContainer getGraphsLazyContainer() {
        return graphsLazyQueryContainer;
    }

    public WebgephiEntityClient getWebgephiClient() {
        return webgephiClient;
    }

    public GraphDetailXml getCurrentGraph() {
        return currentGraph;
    }

    public void refreshGraphList() {
        graphsLazyQueryContainer.refresh();
    }

    public Event<GraphDetailXml> getGraphSelectedEvent() {
        return graphSelectedEvent;
    }

    private void updateSelection(@Observes @Selected GraphDetailXml graph) {
        if (currentGraph != null && graph.getId().equals(currentGraph.getId())) {
            // No update needed
            return;
        }
        currentGraph = graph;
        Notification.show("Graph changed", "Graph: " + graph.getId() + " " + graph.getName(), Notification.Type.TRAY_NOTIFICATION);
    }

    public boolean isLoggedIn() {
        if (webgephiClient == null) {
            return false;
        }

        try {
            webgephiClient.getLoggedUser();
            return true;
        } catch (ErrorHttpResponseException e) {
            log.warn("User not logged: " + e.getMessage(), e);

        } catch (WebgephiClientException e) {
            log.warn("User not logged: " + e.getMessage(), e);
        }
        return false;
    }

    public String getOAuthConsumerKey() {
        String prop = System.getProperty(PROP_OAUTH_CONSUMER_KEY);
        if(prop != null) {
            return prop;
        }
        return "client.webgephi.cz";
    }

    public String getOAuthConsumerSecret() {
        String prop = System.getProperty(PROP_OAUTH_CONSUMER_SECRET);
        if(prop != null) {
            return prop;
        }
        return "68c17d0d-5090-4b0e-bb2f-f4fe50d83704";
    }

    public String getServerUrl() {
        String prop = System.getProperty(PROP_WEBGEPHI_SERVER_URL);
        if(prop != null) {
            if(prop.endsWith("/")){
                prop = prop.substring(0, prop.length() - 1);
            }
            return prop;
        }

        if (getBaseUrl().contains("local")) {
            return UserSession.SERVER_URL_LOCAL;
        } else {
            return UserSession.SERVER_URL_PRODUCTION;
        }
    }

    public String getBaseUrl() {
        if (baseUrl == null) {
            baseUrl = Page.getCurrent().getLocation().toString();
            if (baseUrl.endsWith("login")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - "/#!login".length());
            }
        }
        return baseUrl;
    }

    public String getAuthDeniedMessage() {
        return authDeniedMessage;
    }

    public void setAuthDeniedMessage(String authDeniedMessage) {
        this.authDeniedMessage = authDeniedMessage;
    }

    public String getAuthFailedMessage() {
        return authFailedMessage;
    }

    public void setAuthFailedMessage(String authFailedMessage) {
        this.authFailedMessage = authFailedMessage;
    }
}
