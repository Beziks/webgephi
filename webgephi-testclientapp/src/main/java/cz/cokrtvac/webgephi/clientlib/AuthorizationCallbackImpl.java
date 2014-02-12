package cz.cokrtvac.webgephi.clientlib;

import cz.cokrtvac.webgephi.client.callback.AuthorizationCallback;
import org.slf4j.Logger;

import javax.inject.Inject;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 28.1.14
 * Time: 17:11
 */
public class AuthorizationCallbackImpl implements AuthorizationCallback {
    @Inject
    private Logger log;

    @Inject
    private ConnectionBean connectionBean;

    @Override
    public String onAuthorizationSuccess(String token, String verifier) {
        log.info("onAuthorizationSuccess: " + token + " | " + verifier);
        connectionBean.setVerifier(verifier);
        return "connection.jsf";
    }

    @Override
    public String onAuthorizationDenied(String message) {
        log.info("onAuthorizationDenied: " + message);
        connectionBean.setMessage("Authorization denied: " + message);
        return "connection.jsf";
    }

    @Override
    public String onAuthorizationFailed(String reason) {
        log.info("onAuthorizationFailed: " + reason);
        connectionBean.setMessage("Authorization failed: " + reason);
        return "connection.jsf";
    }
}
