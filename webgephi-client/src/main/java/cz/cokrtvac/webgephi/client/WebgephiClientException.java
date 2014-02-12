package cz.cokrtvac.webgephi.client;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 28.1.14
 * Time: 23:35
 */
public class WebgephiClientException extends Exception {
    public WebgephiClientException() {
    }

    public WebgephiClientException(String message) {
        super(message);
    }

    public WebgephiClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebgephiClientException(Throwable cause) {
        super(cause);
    }

    public WebgephiClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
