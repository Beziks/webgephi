package cz.cokrtvac.webgephi.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.*;
import org.w3c.dom.traversal.NodeFilter;

import java.io.*;

/**
 * Serialization and deserialization of xml
 *
 * @author Vaclav Cokrt
 */
public class XmlFastUtil {
    private static final Logger LOG = LoggerFactory.getLogger(XmlFastUtil.class);

    private static class LSResourceHandler implements LSResourceResolver {
        DOMImplementationLS impl;

        private LSResourceHandler(DOMImplementationLS impl) {
            this.impl = impl;
        }

        @Override
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
            LSInput resource = impl.createLSInput();
            String name = "/META-INF/dtd/" + systemId.substring(systemId.lastIndexOf('/') + 1);
            resource.setByteStream(XmlFastUtil.class.getResourceAsStream(name));
            return resource;
        }
    }

    public static String lsSerializeDom(Node doc) throws Exception {
        if (doc == null)
            return null;
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        DOMImplementationLS impl = (DOMImplementationLS)
                registry.getDOMImplementation("LS");

        LSSerializer writer = impl.createLSSerializer();
        return writer.writeToString(doc);
    }

    public static String lsSerializeDom(Node doc, String encoding) throws Exception {
        if (doc == null)
            return null;
        return new String(lsSerializeDom(doc, new ByteArrayOutputStream(), encoding).toByteArray(), encoding);
    }


    public static <T extends OutputStream> T lsSerializeDom(Node doc, T byteStream, String encoding) throws Exception {
        if (doc == null)
            return byteStream;
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");

        LSOutput lsOutput = impl.createLSOutput();
        lsOutput.setByteStream(byteStream);
        encoding = encoding == null ? "UTF-8" : encoding;
        lsOutput.setEncoding(encoding);

        impl.createLSSerializer().write(doc, lsOutput);

        return byteStream;
    }

    public static String lsSerializeDomPretty(Node doc) throws Exception {
        if (doc == null)
            return null;
        return lsSerializeDomPretty(doc, "UTF-8");
    }

    public static String lsSerializeDomPretty(Node doc, String encoding) throws Exception {
        if (doc == null)
            return null;
        return lsSerializeDomPretty(doc, encoding, true);
    }

    public static String lsSerializeDomPretty(Node doc, String encoding, boolean withXMLDeclaration) throws Exception {
        if (doc == null)
            return null;
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();

        DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");

        LSSerializer writer = impl.createLSSerializer();
        LSOutput output = impl.createLSOutput();

        DOMConfiguration domConfiguration = writer.getDomConfig();
        if (domConfiguration.canSetParameter("format-pretty-print", Boolean.TRUE)) {
            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
        } else {
            if (LOG.isDebugEnabled())
                LOG.debug("DOM Configuration does not support 'format-pretty-print': " + domConfiguration.getClass().getName());
        }

        StringWriter stringWriter = new StringWriter();
        output.setEncoding(encoding);
        output.setCharacterStream(stringWriter);
        writer.write(doc, output);
        String ret = stringWriter.toString();
        if (withXMLDeclaration) {
            ret = ret.replaceFirst("><", ">" + writer.getNewLine() + "<");
        } else {
            ret = ret.substring(ret.indexOf(">") + 1);
        }
        return ret;
    }

    public static Document lsDeSerializeDom(String data) throws Exception {
        if (data == null)
            return null;
        return lsDeSerializeDom(new StringReader(data), true);
    }

    public static Document lsDeSerializeDom(String data, boolean namespaceAware) throws Exception {
        return lsDeSerializeDom(new StringReader(data), namespaceAware);
    }

    public static Document lsDeSerializeDom(InputStream byteStream) throws Exception {
        return lsDeSerializeDom(byteStream, true);
    }

    public static Document lsDeSerializeDom(InputStream byteStream, boolean namespaceAware) throws Exception {
        InputData inputData = new InputData();
        inputData.setInputStream(byteStream);
        return lsDeserializeDom(inputData, namespaceAware);
    }

    public static Document lsDeSerializeDom(Reader characterStream) throws Exception {
        return lsDeSerializeDom(characterStream, true);
    }

    public static Document lsDeSerializeDom(byte[] data) throws Exception {
        if (data == null)
            return null;
        return lsDeSerializeDom(data, true);
    }

    public static Document lsDeSerializeDom(byte[] data, boolean namespaceAware) throws Exception {
        if (data == null)
            return null;
        // UTF-8 BOM = ef bb bf
        if (data[0] == (byte) 0xEF && data[1] == (byte) 0xBB && data[2] ==
                (byte) 0xBF) {
            byte[] trimmed = new byte[data.length - 3];
            System.arraycopy(data, 3, trimmed, 0, data.length - 3);

            return lsDeSerializeDom(new ByteArrayInputStream(trimmed), namespaceAware);
        }
        return lsDeSerializeDom(new ByteArrayInputStream(data), namespaceAware);
    }

    public static Document lsDeSerializeDom(Reader characterStream, boolean namespaceAware) throws Exception {
        InputData inputData = new InputData();
        inputData.setReader(characterStream);
        return lsDeserializeDom(inputData, namespaceAware);
    }

    private static Document lsDeserializeDom(InputData inputData, boolean namespaceAware) throws Exception {
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        DOMImplementationLS impl = (DOMImplementationLS)
                registry.getDOMImplementation("LS");

        LSParser parser = impl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, "http://www.w3.org/2001/XMLSchema");
        parser.getDomConfig().setParameter("resource-resolver", new LSResourceHandler(impl));
        parser.getDomConfig().setParameter("error-handler", new DOMErrorHandler() {
            @Override
            public boolean handleError(DOMError error) {
                LOG.warn("Error parsing XML: " + error.getMessage());
                return false;
            }
        });
        if (!namespaceAware) {
            parser.getDomConfig().setParameter("namespaces", namespaceAware);
        }
        parser.setFilter(new LSParserFilter() {

            @Override
            public short startElement(Element elementArg) {
                return FILTER_ACCEPT;
            }

            @Override
            public int getWhatToShow() {
                return NodeFilter.SHOW_COMMENT | NodeFilter.SHOW_TEXT;
            }

            @Override
            public short acceptNode(Node nodeArg) {
                if (nodeArg instanceof Text) {
                    if (nodeArg.getNodeValue().trim().length() > 0)
                        return FILTER_ACCEPT;
                }
                return FILTER_SKIP;
            }
        });

        LSInput input = impl.createLSInput();
        inputData.setLSDataSource(input);
        Document doc = parser.parse(input);
        return doc;
    }

    private static class InputData {
        Reader reader;
        InputStream stream;

        void setReader(Reader reader) {
            this.reader = reader;
        }

        void setInputStream(InputStream stream) {
            this.stream = stream;
        }

        LSInput setLSDataSource(LSInput lsInput) {
            if (reader != null) {
                lsInput.setCharacterStream(reader);
            } else if (stream != null) {
                lsInput.setByteStream(stream);
            } else {
                throw new NullPointerException("Neither a reader nor an input stream have been set.");
            }
            return lsInput;
        }
    }
}