package cz.cokrtvac.webgephi.api.model.ranking;

import cz.cokrtvac.webgephi.api.model.AbstractFunctionXml;
import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 3.6.13
 * Time: 18:20
 */
@XmlRootElement(name = "ranking")
public class RankingXml extends AbstractFunctionXml {
    @XmlElementRef
    private RESTServiceDiscovery restServiceDiscovery;
}
