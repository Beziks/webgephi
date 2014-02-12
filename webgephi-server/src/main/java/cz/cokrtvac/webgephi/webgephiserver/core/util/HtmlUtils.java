package cz.cokrtvac.webgephi.webgephiserver.core.util;

import cz.cokrtvac.webgephi.api.util.Log;
import cz.cokrtvac.webgephi.api.util.XmlUtil;
import org.apache.xpath.XPathAPI;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class HtmlUtils {
    private static Logger log = Log.get(HtmlUtils.class);

    /**
     * Makes all png files embeded
     *
     * @param html
     * @return
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws java.io.IOException
     * @throws javax.xml.transform.TransformerException
     */
    public static String embededImages(String html) throws SAXException, ParserConfigurationException, IOException, TransformerException {
        log.trace("Cleaning html:\n " + html);
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        TagNode n = cleaner.clean(new StringReader(html));
        Document doc = new DomSerializer(props).createDOM(n);

        log.trace("Embeding png images");
        NodeList nl = XPathAPI.selectNodeList(doc, "//img/@src");
        for (int i = 0; i < nl.getLength(); i++) {
            String s = nl.item(i).getTextContent();
            log.trace("Processing file " + s);

            if (!s.startsWith("file:") || !s.endsWith(".png")) {
                log.info("Not png file, skipping: " + s);
                continue;
            }

            s = s.substring(5);
            String base64 = ImageUtils.pngToEmbededBase64(new File(s));
            nl.item(i).setNodeValue(base64);
        }

        return XmlUtil.toString(doc).replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<!DOCTYPE html>");
    }


    public static void main(String[] args) throws SAXException, ParserConfigurationException, IOException, TransformerException {
        String inputHtml = "<HTML> <BODY> <h1>Eigenvector Centrality Report</h1> <hr><h2> Parameters: </h2>Network Interpretation:  undirected<br>Number of iterations: 100<br>Sum change: 0.009429377861440071<br> <h2> Results: </h2><IMG SRC=\"file:C:\\Users\\beziks\\AppData\\Local\\Temp\\temp7513478020397302488111180292539843\\eigenvector-centralities.png\" WIDTH=\"600\" HEIGHT=\"400\" BORDER=\"0\" USEMAP=\"#chart\"></IMG></BODY></HTML>";
        System.out.println(embededImages(inputHtml));
    }
}
