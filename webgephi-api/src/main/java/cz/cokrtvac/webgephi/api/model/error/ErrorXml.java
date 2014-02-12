package cz.cokrtvac.webgephi.api.model.error;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 3.6.13
 * Time: 18:23
 */

@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.NONE)
public class ErrorXml {
    @XmlElement(required = true, nillable = false)
    private Code code;
    @XmlElement(required = true, nillable = false)
    private String message;
    @XmlElement
    private String detail;

    public ErrorXml() {
    }

    public ErrorXml(Response.Status status, String message) {
        this(status, message, null);
    }

    public ErrorXml(Response.Status status, String message, String detail) {
        this.code = new Code(status);
        this.message = message;
        this.detail = detail;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
