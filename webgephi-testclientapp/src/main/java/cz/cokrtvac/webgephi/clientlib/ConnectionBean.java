package cz.cokrtvac.webgephi.clientlib;

import cz.cokrtvac.webgephi.client.Token;
import cz.cokrtvac.webgephi.client.WebgephiAuthenticator;
import cz.cokrtvac.webgephi.client.WebgephiAuthenticatorException;
import cz.cokrtvac.webgephi.client.WebgephiClient;
import cz.cokrtvac.webgephi.client.util.UrlUtil;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 9.2.14
 * Time: 13:55
 */
@Named
@SessionScoped
public class ConnectionBean implements Serializable {
    @Inject
    private Logger log;

    private String serverBase = "https://www.webgephi.cz";
    private String serverBaseLocal = "https://localhost:8443";
    private String clientBase;
    private String clientKey = "client.webgephi.cz";
    private String clientSecret = "68c17d0d-5090-4b0e-bb2f-f4fe50d83704";

    private Set<String> scopes = new HashSet<String>();

    private WebgephiAuthenticator authenticator;
    private WebgephiClient webgephiClient;

    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @PostConstruct
    private void init() {
        log.info("INIT CONNECTION BEAN: " + getClass().getName() + " | " + System.identityHashCode(this));

        clientBase = getApplicationUrl();
        if (clientBase.contains("localhost")) {
            serverBase = serverBaseLocal;
        }

        log.debug("Server base: " + serverBase);
        log.debug("Client base: " + clientBase);
    }

    private String getApplicationUrl() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String contextURL = request.getRequestURL().toString().replace(request.getRequestURI().substring(0), "") + request.getContextPath();
        return contextURL;
    }

    // Login --------------------------------------------
    public void getAccessToken() throws WebgephiAuthenticatorException, IOException {
        authenticator = new WebgephiAuthenticator(
                clientKey, clientSecret,
                serverBase,
                clientBase
        );

        String url = authenticator.authorize(getScopes().toArray(new String[]{}));
        log.info("Redirecting to " + url);
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    public void setVerifier(String verifier) {
        try {
            Token accessToken = authenticator.obtainAccessToken(verifier);
            webgephiClient = new WebgephiClient(UrlUtil.concat(serverBase, "rest"), accessToken);
            setMessage("Authorization success");
        } catch (WebgephiAuthenticatorException e) {
            log.error("Request for access token failed", e);
            setMessage("Request for access token failed");
        }
    }

    // GETTERS and SETTERS ===========================
    public WebgephiClient getWebgephiClient() {
        return webgephiClient;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    public String getServerBase() {
        return serverBase;
    }

    public void setServerBase(String serverBase) {
        this.serverBase = serverBase;
    }

    public String getClientBase() {
        return clientBase;
    }

    public void setClientBase(String clientBase) {
        this.clientBase = clientBase;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    // INFO ============================================
    public String getAccessTokenInfo() {
        if (webgephiClient == null) {
            return "No access token yet, you have to log in";
        }

        String out = "<br />Token: " + webgephiClient.getAccessToken().getToken() +
                "<br />Secret: " + webgephiClient.getAccessToken().getSecret() +
                "<br />Scopes:";
        for (String s : getScopes()) {
            out += " " + s;
        }
        return out;
    }

    // LOG out ============================================
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "connection.jsf";
    }
}
