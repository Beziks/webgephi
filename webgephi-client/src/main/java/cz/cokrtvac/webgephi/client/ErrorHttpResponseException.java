package cz.cokrtvac.webgephi.client;

import cz.cokrtvac.webgephi.api.model.error.ErrorXml;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 28.1.14
 * Time: 23:35
 */
public class ErrorHttpResponseException extends Exception {
    private ErrorXml error;

    public ErrorHttpResponseException(ErrorXml error) {
        super(error.toString());
        this.error = error;
    }
}
