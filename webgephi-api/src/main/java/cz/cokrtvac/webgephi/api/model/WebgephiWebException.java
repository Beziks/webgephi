package cz.cokrtvac.webgephi.api.model;

import cz.cokrtvac.webgephi.api.model.error.ErrorXml;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 30.1.14
 * Time: 19:33
 */
public class WebgephiWebException extends WebApplicationException {
    public WebgephiWebException(Response.Status status, String message, String detail) {
        super(Response.status(status).entity(new ErrorXml(status, message, detail)).build());
    }

    public WebgephiWebException(Response.Status status, String message, Exception cause) {
        super(cause, Response.status(status).entity(new ErrorXml(status, message, cause.getMessage())).build());
    }

    public WebgephiWebException(Response.Status status, String message) {
        super(Response.status(status).entity(new ErrorXml(status, message)).build());
    }
}
