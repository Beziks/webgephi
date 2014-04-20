package cz.cokrtvac.webgephi.api.model.ranking;

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
@XmlRootElement(name = RankingsXml.PATH)
@XmlAccessorType(XmlAccessType.NONE)
public class RankingsXml {
    public static final String PATH = "rankings";
    private List<RankingXml> rankings;

    @XmlElement(name = "ranking")
    public List<RankingXml> getRankings() {
        if (rankings == null) {
            rankings = new ArrayList<RankingXml>();
        }
        return rankings;
    }

    public void setRankings(List<RankingXml> rankings) {
        this.rankings = rankings;
    }

    @Override
    public String toString() {
        return "RankingsXml " + CollectionsUtil.toString(getRankings());
    }

    @XmlElementRef
    private RESTServiceDiscovery restServiceDiscovery;
}
