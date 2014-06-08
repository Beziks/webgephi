package cz.cokrtvac.webgephi.api.model.statistic;

import cz.cokrtvac.webgephi.api.model.AbstractFunctionsXml;
import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 10:35
 */
@XmlRootElement(name = "statistics")
@XmlAccessorType(XmlAccessType.NONE)
public class StatisticsXml extends AbstractFunctionsXml<StatisticXml> {
    @Override
    @XmlElement(name = "statistic")
    public List<StatisticXml> getFunctions() {
        return super.getFunctions();
    }

    @Override
    public void setFunctions(List<StatisticXml> functions) {
        super.setFunctions(functions);
    }

    @XmlElementRef
    private RESTServiceDiscovery restServiceDiscovery;
}
