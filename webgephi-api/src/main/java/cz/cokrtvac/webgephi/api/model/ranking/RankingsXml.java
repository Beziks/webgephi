package cz.cokrtvac.webgephi.api.model.ranking;

import cz.cokrtvac.webgephi.api.model.AbstractFunctionsXml;
import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 10:35
 */
@XmlRootElement(name = "rankings")
@XmlAccessorType(XmlAccessType.NONE)
public class RankingsXml extends AbstractFunctionsXml<RankingXml> {

    @Override
    @XmlElement(name = "ranking")
    public List<RankingXml> getFunctions() {
        return super.getFunctions();
    }

    @Override
    public void setFunctions(List<RankingXml> functions) {
        super.setFunctions(functions);
    }

    @XmlElementRef
    private RESTServiceDiscovery restServiceDiscovery;
}
