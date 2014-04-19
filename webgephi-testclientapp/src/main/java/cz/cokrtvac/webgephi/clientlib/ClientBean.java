package cz.cokrtvac.webgephi.clientlib;

import cz.cokrtvac.webgephi.api.util.XmlUtil;
import cz.cokrtvac.webgephi.client.WebgephiClientException;
import cz.cokrtvac.webgephi.client.WebgephiOAuthClient;
import org.slf4j.Logger;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 5.1.14
 * Time: 19:05
 */
@Named
@SessionScoped
public class ClientBean implements Serializable {
    @Inject
    private Logger log;

    @Inject
    private ConnectionBean connectionBean;

    private String message;

    // Request
    private String method;
    private List<String> methods = Arrays.asList(new String[]{"GET", "POST", "PUT"});

    private String resource = "layouts";
    private String body;

    // Response
    private String status;
    private String responseBody;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String sendRequest() throws WebgephiClientException {
        WebgephiOAuthClient client = connectionBean.getWebgephiClient();
        if (client == null) {
            setMessage("Client is null, you have to log in first");
            return null;
        }

        Response response = null;

        if ("GET".equals(method)) {
            response = client.get(resource);
        } else if ("PUT".equals(method)) {
            response = client.put(resource, null, body);
        } else if ("POST".equals(method)) {
            response = client.post(resource, null, body);
        } else {
            setMessage("Error");
            return null;
        }

        setStatus(response.getStatus() + " (" + response.getStatusInfo().getReasonPhrase() + ")");
        setResponseBody(XmlUtil.prettifyXml(response.readEntity(String.class)).trim());
        response.close();

        log.info(getResponseBody());

        setMessage("Request done");
        return null;
    }


    // GETTERS and SETTERS
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
