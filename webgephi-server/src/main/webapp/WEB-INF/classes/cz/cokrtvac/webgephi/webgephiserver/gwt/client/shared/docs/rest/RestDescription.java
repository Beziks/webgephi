package cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.docs.rest;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 12.2.14
 * Time: 13:49
 */
@Portable
@Bindable
public class RestDescription {
    private String name;
    private String description;
    private String method;
    private String resource;
    private String authentication;

    private String sampleRequest;
    private String sampleRequestBody;

    private String sampleResponseBody;

    public RestDescription() {
    }

    public RestDescription(String name, String description, String method, String resource, String authentication, String sampleRequest, String sampleRequestBody, String sampleResponseBody) {
        this.name = name;
        this.description = description;
        this.method = method;
        this.resource = resource;
        this.authentication = authentication;
        this.sampleRequest = sampleRequest;
        this.sampleRequestBody = sampleRequestBody;
        this.sampleResponseBody = sampleResponseBody;
    }

    public RestDescription(String name, String description, String method, String resource, String authentication, String sampleRequest, String sampleResponseBody) {
        this(name, description, method, resource, authentication, sampleRequest, "EMPTY", sampleResponseBody);
    }

    public String getName() {
        return name;
    }

    public String getMethod() {
        return method;
    }

    public String getResource() {
        return resource;
    }

    public String getAuthentication() {
        return authentication;
    }

    public String getSampleRequest() {
        return sampleRequest;
    }

    public String getSampleRequestBody() {
        return sampleRequestBody;
    }

    public String getSampleResponseBody() {
        return sampleResponseBody;
    }

    public String getDescription() {
        return description;
    }
}
