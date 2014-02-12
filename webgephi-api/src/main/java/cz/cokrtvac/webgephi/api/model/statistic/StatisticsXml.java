package cz.cokrtvac.webgephi.api.model.statistic;

import cz.cokrtvac.webgephi.api.util.CollectionsUtil;
import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 10:35
 */
@XmlRootElement(name = "statistics")
@XmlAccessorType(XmlAccessType.NONE)
public class StatisticsXml {
    private List<StatisticXml> statistics;

    @XmlElement(name = "statistic")
    public List<StatisticXml> getStatistics() {
        if (statistics == null) {
            statistics = new ArrayList<StatisticXml>();
        }
        return statistics;
    }

    public void setStatistics(List<StatisticXml> statistics) {
        this.statistics = statistics;
    }

    @Override
    public String toString() {
        return "StatisticsXml " + CollectionsUtil.toString(getStatistics());
    }

    @XmlElementRef
    private RESTServiceDiscovery restServiceDiscovery;
}
