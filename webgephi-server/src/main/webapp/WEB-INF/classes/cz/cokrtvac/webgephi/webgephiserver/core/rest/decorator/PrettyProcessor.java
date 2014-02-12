package cz.cokrtvac.webgephi.webgephiserver.core.rest.decorator;

import cz.cokrtvac.webgephi.api.util.Log;
import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.spi.interception.DecoratorProcessor;
import org.slf4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.lang.annotation.Annotation;

@DecorateTypes({"text/*+xml", "application/*+xml", MediaType.APPLICATION_XML, MediaType.TEXT_XML})
public class PrettyProcessor implements DecoratorProcessor<Marshaller, Pretty> {
    private final Logger LOG = Log.get(PrettyProcessor.class);

    @Override
    public Marshaller decorate(Marshaller target, Pretty annotation, Class type, Annotation[] annotations, MediaType mediaType) {
        LOG.info("Make output xml pretty.");
        try {
            target.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch (PropertyException e) {
        }
        return target;
    }
}
