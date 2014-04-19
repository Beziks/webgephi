package cz.cokrtvac.webgephi.webgephiserver.core.rest.custom_writter;

import cz.cokrtvac.webgephi.api.model.error.ErrorXml;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 8.2.14
 * Time: 22:23
 */
@Provider
@Produces({MediaType.TEXT_HTML})
public class ErrorHtmlBodyWritter implements MessageBodyWriter<ErrorXml> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return ErrorXml.class.equals(type);
    }

    @Override
    public long getSize(ErrorXml errorXml, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(ErrorXml errorXml, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        entityStream.write(
                (
                        "<html><head><title>Error " + errorXml.getCode().getNumber() + " (" + errorXml.getCode().getCode() + ")</title></head>" +
                                "<body>" +
                                "<h1>" + errorXml.getCode().getNumber() + " (" + errorXml.getCode().getCode() + ")</h1>" +
                                "<ul class=\"webgephiErrorReport\">" +
                                "<li>Message: <span class=\"webgephiErrorMessage\">" + errorXml.getMessage() + "</span></li>" +
                                "<li>Detail: <span class=\"webgephiErrorDetail\">" + errorXml.getDetail() + "</span></li>" +
                                "</ul></body></html>"

                ).getBytes()
        );
    }
}
