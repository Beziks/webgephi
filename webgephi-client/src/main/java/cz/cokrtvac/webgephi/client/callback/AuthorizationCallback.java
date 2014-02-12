package cz.cokrtvac.webgephi.client.callback;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 28.1.14
 * Time: 17:10
 */
public interface AuthorizationCallback {
    /**
     * This method will be called, if user successfully authorized request token.
     *
     * @param token    - request token key
     * @param verifier - request token key
     * @return URL where user should be redirected after end of this method. Relative to application base.
     */
    public String onAuthorizationSuccess(String token, String verifier);

    /**
     * This method will be called, if user canceled authorization.
     *
     * @param message - Info like "User canceled authorization"
     * @return URL where user should be redirected after end of this method. Relative to application base.
     */
    public String onAuthorizationDenied(String message);

    /**
     * This method will be called, if authorization failed - maybe because of invalid token or internal server error.
     *
     * @param reason - May contain some other information about error.
     * @return URL where user should be redirected after end of this method. Relative to application base.
     */
    public String onAuthorizationFailed(String reason);
}
