package cz.cokrtvac.webgephi.client;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 28.1.14
 * Time: 21:05
 */
public class WebgephiAuthenticatorException extends Exception {
    public WebgephiAuthenticatorException() {
    }

    public WebgephiAuthenticatorException(String message) {
        super(message);
    }

    public WebgephiAuthenticatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebgephiAuthenticatorException(Throwable cause) {
        super(cause);
    }

    public WebgephiAuthenticatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
