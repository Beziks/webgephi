package cz.cokrtvac.webgephi.clientapp.model;

import cz.cokrtvac.webgephi.client.callback.AuthorizationCallback;

import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5. 4. 2014
 * Time: 19:55
 */
public class WebgephiAuthorizationCallback implements AuthorizationCallback {
    @Inject
    private UserSession userSession;

    @Override
    public String onAuthorizationSuccess(String token, String verifier) {
        userSession.setVerifier(verifier);
        userSession.setAuthDeniedMessage(null);
        userSession.setAuthFailedMessage(null);
        return "";
    }

    @Override
    public String onAuthorizationDenied(String message) {
        userSession.setAuthFailedMessage(null);
        userSession.setAuthDeniedMessage(message);
        return "";
    }

    @Override
    public String onAuthorizationFailed(String reason) {
        userSession.setAuthFailedMessage(reason);
        userSession.setAuthDeniedMessage(null);
        return "";
    }
}
