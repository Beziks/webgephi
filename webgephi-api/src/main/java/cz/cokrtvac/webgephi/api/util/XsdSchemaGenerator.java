package cz.cokrtvac.webgephi.api.util;

import cz.cokrtvac.webgephi.api.model.GraphFunctionXml;
import cz.cokrtvac.webgephi.api.model.JaxbConstants;
import cz.cokrtvac.webgephi.api.model.error.ErrorXml;
import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.api.model.filter.FiltersXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphsXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutsXml;
import cz.cokrtvac.webgephi.api.model.ranking.RankingXml;
import cz.cokrtvac.webgephi.api.model.ranking.RankingsXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticsXml;
import cz.cokrtvac.webgephi.api.model.user.UserXml;
import cz.cokrtvac.webgephi.api.model.user.UsersXml;
import org.apache.xpath.XPathAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 6. 6. 2014
 * Time: 22:59
 */
public class XsdSchemaGenerator {
    public static final Logger LOG = LoggerFactory.getLogger(XsdSchemaGenerator.class);

    public static void main(String[] args) throws Exception {
        XsdSchemaGenerator gen = new XsdSchemaGenerator(
                // RESTServiceDiscovery.AtomLink.class,
                GraphFunctionXml.class,
                ErrorXml.class,
                UserXml.class,
                UsersXml.class,
                GraphDetailXml.class,
                GraphsXml.class,
                LayoutXml.class,
                LayoutsXml.class,
                StatisticXml.class,
                StatisticsXml.class,
                RankingXml.class,
                RankingsXml.class,
                FilterXml.class,
                FiltersXml.class
        );
        LOG.info("DONE");
    }

    public XsdSchemaGenerator(Class<?>... jaxbRoots) throws Exception {
        LOG.info("Generating XSD for classes " + jaxbRoots);
        generate(jaxbRoots);
        LOG.info("XSD for classes " + jaxbRoots + " generated");
    }

    private void generate(Class<?>... roots) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(roots);
        final List<DOMResult> results = new ArrayList<DOMResult>();

        jc.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                DOMResult result = new DOMResult();
                if (namespaceUri.contains("Atom")) {
                    result.setSystemId(JaxbConstants.EXTENDED_ATOM_LOCATION);
                } else if (namespaceUri.contains("WebGephi")) {
                    result.setSystemId("WebGephiSchema.xsd");
                } else {
                    result.setSystemId("xxx");
                }
                results.add(result);
                return result;
            }
        });

        for (DOMResult domResult : results) {
            if (domResult.getSystemId().contains("Atom")) {
                postProcessExtendedAtom(domResult);
            } else if (domResult.getSystemId().contains("WebGephi")) {
                postProcessWebgephiSchema(domResult);
            }
        }
    }

    private void postProcessExtendedAtom(DOMResult domResult) throws Exception {
        Document doc = (Document) domResult.getNode();
        Element imp = (Element) XPathAPI.selectSingleNode(doc.getDocumentElement(), "//xs:import");

        Element incl = doc.createElement("xs:include");
        incl.setAttribute("schemaLocation", "http://www.kbcafe.com/rss/atom.xsd.xml");
        doc.getDocumentElement().replaceChild(incl, imp);

        Element link = (Element) XPathAPI.selectSingleNode(doc.getDocumentElement(), "//xs:element[@name='link']");
        link.setAttribute("type", "linkType");

        IOUtil.write(new File("./webgephi-api/src/main/resources", "ExtendedAtom.xsd"), XmlUtil.toString(doc).getBytes());
    }

    private void postProcessWebgephiSchema(DOMResult domResult) throws Exception {
        Document doc = (Document) domResult.getNode();
        Element atomLink = (Element) XPathAPI.selectSingleNode(doc.getDocumentElement(), "//xs:import[@schemaLocation='xxx']");
        atomLink.getParentNode().removeChild(atomLink);

        IOUtil.write(new File("./webgephi-api/src/main/resources", domResult.getSystemId()), XmlUtil.toString(doc).getBytes());
    }
}
