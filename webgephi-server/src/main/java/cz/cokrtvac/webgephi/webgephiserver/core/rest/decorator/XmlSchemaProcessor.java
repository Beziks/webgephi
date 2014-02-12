package cz.cokrtvac.webgephi.webgephiserver.core.rest.decorator;

import cz.cokrtvac.webgephi.api.util.Log;
import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.spi.interception.DecoratorProcessor;
import org.slf4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.Marshaller;
import java.lang.annotation.Annotation;

@DecorateTypes({"text/*+xml", "application/*+xml", MediaType.APPLICATION_XML, MediaType.TEXT_XML})
public class XmlSchemaProcessor implements DecoratorProcessor<Marshaller, XmlSchema> {
    private static final Logger log = Log.get(XmlSchemaProcessor.class);

    @Override
    public Marshaller decorate(Marshaller target, XmlSchema annotation, Class type, Annotation[] annotations, MediaType mediaType) {
        log.debug("Attach xml schema for: " + type);
        return target;
    }
}
