package cz.cokrtvac.webgephi.client.util;

import cz.cokrtvac.webgephi.api.util.Log;
import org.slf4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 8.3.14
 * Time: 16:32
 */
public class JAXBUtil {
    private static final Logger log = Log.get(JAXBUtil.class);
    private static final Map<Class<?>, JAXBContext> cache = new ConcurrentHashMap<Class<?>, JAXBContext>();

    private static JAXBContext getContext(Class<?> clazz) throws JAXBException {
        if (!cache.containsKey(clazz)) {
            cache.put(clazz, JAXBContext.newInstance(clazz));
        }
        return cache.get(clazz);

    }

    public static <T> String marshall(Class<T> entiyClass, T entiy) throws JAXBException {
        Marshaller jaxbMarshaller = getContext(entiyClass).createMarshaller();
        StringWriter writer = new StringWriter();
        jaxbMarshaller.marshal(entiy, writer);
        return writer.toString();
    }

    public static <T> T unmarshall(Class<T> entiyClass, String xml) throws JAXBException {
        Unmarshaller jaxbMarshaller = getContext(entiyClass).createUnmarshaller();
        StringWriter writer = new StringWriter();
        T result = (T) jaxbMarshaller.unmarshal(new StringReader(xml));
        log.debug("XML unmarshalled to " + result);
        return result;
    }
}
