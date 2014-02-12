package cz.cokrtvac.webgephi.api.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 16:50
 */
public class XmlUtil {
    private static Logger log = LoggerFactory.getLogger(XmlUtil.class);

    public static Document stringToDom(String xmlSource) throws Exception {
        try {
            return XmlFastUtil.lsDeSerializeDom(xmlSource);
        } catch (Exception e) {
            log.error("String cannot be transformed to DOM", e);
            throw e;
        }
    }

    public static String toString(Document doc) {
        try {
            return XmlFastUtil.lsSerializeDomPretty(doc);
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }

    public static String prettifyXml(String xml) {
        try {
            return toString(stringToDom(xml)).trim();
        } catch (Exception e) {
            return xml;
        }
    }


}
