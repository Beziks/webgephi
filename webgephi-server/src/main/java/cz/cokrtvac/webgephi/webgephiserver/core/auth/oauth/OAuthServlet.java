package cz.cokrtvac.webgephi.webgephiserver.core.auth.oauth;

import cz.cokrtvac.webgephi.api.util.Log;
import org.jboss.resteasy.auth.oauth.OAuthProvider;
import org.jboss.resteasy.auth.oauth.OAuthUtils;
import org.slf4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 26.1.14
 * Time: 16:12
 */
public class OAuthServlet extends org.jboss.resteasy.auth.oauth.OAuthServlet {
    private Logger log = Log.get(getClass());

    final static String TOKEN_AUTHORIZATION_CONFIRM_URL = "/authorization/confirm";

    final static String PARAM_TOKEN_AUTHORIZATION_URL = "oauth.provider.tokens.authorization";
    final static String PARAM_AUTHORIZATION_REDIRECT = "authorization.redirect";
    final static String PARAM_AUTHORIZATION_REDIRECT_ANCHOR = "authorization.redirect.anchor";

    private String authorizationURL = "/authorization";
    private String authorizationRedirect;
    private String authorizationRedirectAnchor = "";

    OAuthProvider provider;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();

        provider = OAuthUtils.getOAuthProvider(context);

        authorizationURL = context.getInitParameter(PARAM_TOKEN_AUTHORIZATION_URL);
        if (authorizationURL == null) {
            authorizationURL = "/authorization";
        }

        authorizationRedirect = config.getInitParameter(PARAM_AUTHORIZATION_REDIRECT);
        if (authorizationRedirect == null) {
            authorizationRedirect = "redirectNotDefined.jsp";
        }

        if (authorizationRedirect.startsWith("/")) {
            authorizationRedirect = authorizationRedirect.substring(1, authorizationRedirect.length());
        }

        authorizationRedirectAnchor = config.getInitParameter(PARAM_AUTHORIZATION_REDIRECT_ANCHOR);
        if (authorizationRedirectAnchor == null) {
            authorizationRedirectAnchor = "";
        }

        log.info("Authorization url: " + authorizationURL);
        log.info("Authorization redirect: " + authorizationRedirect);
        log.info("Authorization redirect anchor: " + authorizationRedirectAnchor);
    }


    @Override
    protected void service(HttpServletRequest req,
                           HttpServletResponse resp)
            throws ServletException,
            IOException {
        String pathInfo = req.getPathInfo();
        log.debug("Serving " + pathInfo);
        log.debug("Query " + req.getQueryString());

        if (pathInfo.equals(authorizationURL)) {
            serveTokenAuthorization(req, resp);
        } else if (pathInfo.startsWith(TOKEN_AUTHORIZATION_CONFIRM_URL)) {
            resp.setStatus(Response.Status.NOT_FOUND.getStatusCode());
        } else {
            super.service(req, resp);
        }
    }

    private void serveTokenAuthorization(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.debug("Consumer token authorization request");

        try {
            String redirect = req.getContextPath() + "/" + authorizationRedirect + "?" + req.getQueryString() + authorizationRedirectAnchor;
            log.info("Redirecting to " + redirect);
            resp.sendRedirect(redirect);
        } catch (Exception x) {
            log.error("Exception ", x);
            OAuthUtils.makeErrorResponse(resp, x.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR, provider);
        }
    }
}
