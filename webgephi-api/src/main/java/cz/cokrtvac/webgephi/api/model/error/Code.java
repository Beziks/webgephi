package cz.cokrtvac.webgephi.api.model.error;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 30.1.14
 * Time: 16:18
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Code {
    @XmlValue
    private String code;
    @XmlAttribute(required = true)
    private int number;

    public Code() {
    }

    public Code(Response.Status status) {
        this(status.getStatusCode(), status.getReasonPhrase());
    }

    public Code(int number, String code){
        this.number = number;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return code + " (" + number + ")";
    }
}
