package cz.cokrtvac.webgephi.client.callback;

import net.oauth.OAuth;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * There, result of request token authorization will be sent
 */
@WebServlet(name = "OAuthTokenAuthorizationCallback", urlPatterns = {"/oauth/callback"})
public class OAuthTokenAuthorizationCallback extends HttpServlet {
    public static final String PARAM_TOKEN = OAuth.OAUTH_TOKEN;
    public static final String PARAM_VERIFIER = OAuth.OAUTH_VERIFIER;
    public static final String PARAM_DENIED = "denied";
    public static final String PARAM_FAILED = "failed";

    @Inject
    private Logger log;

    @Inject
    private AuthorizationCallback authorizationCallback;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter(PARAM_TOKEN);
        String verifier = req.getParameter(PARAM_VERIFIER);
        String denied = req.getParameter(PARAM_DENIED);
        String failed = req.getParameter(PARAM_FAILED);
        log.debug("OAuth callback query string: " + req.getQueryString());
        log.debug("OAuth callback: " + token + " | " + verifier + " | " + denied + " | " + failed);

        if (token != null && verifier != null) {
            log.debug("Authorization success.");
            String url = authorizationCallback.onAuthorizationSuccess(token, verifier);
            redirect(url, req, resp);
        } else if (denied != null) {
            log.debug("Authorization denied by user.");
            String url = authorizationCallback.onAuthorizationDenied(denied);
            redirect(url, req, resp);
        } else if (failed != null) {
            log.debug("Authorization failed.");
            String url = authorizationCallback.onAuthorizationFailed(failed);
            redirect(url, req, resp);
        } else {
            log.warn("No required parameter found, user will be redirected to base URL");
            redirect("", req, resp);
        }
    }

    private void redirect(String url, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(url.startsWith("/")){
            url = url.substring(1, url.length());
        }
        String redirect = req.getContextPath() + "/" + url;
        log.info("Redirecting to " + redirect);
        resp.sendRedirect(redirect);
    }
}
