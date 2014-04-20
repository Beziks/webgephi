package cz.cokrtvac.webgephi.webgephiserver.core.gephi.ranking;

import cz.cokrtvac.webgephi.api.model.PropertyXml;
import cz.cokrtvac.webgephi.api.model.ranking.RankingXml;
import cz.cokrtvac.webgephi.api.model.ranking.RankingsXml;
import org.gephi.ranking.api.Ranking;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.*;

/**
 * Provides info about all available layout functions in application.
 * Runs after deploy.
 * <p/>
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 15:18
 */
@Singleton
@Startup
public class RankingsPool {
    @Inject
    private Logger log;

    private RankingsXml rankings;
    private Map<String, RankingXml> rankingXmlMap = new HashMap<String, RankingXml>();


    @PostConstruct
    public void init() {
        log.info("Initializing app setting.");
        initMaps();
        initRankings();
        log.info("Initializing done.");
    }

    public RankingsXml getAvailableRankings() {
        return rankings;
    }

    private void initMaps() {
        // Manually add possible configurations
        // Color ranking
        RankingXml colorRanking = createRankingXml(RankingXml.COLOR_RANKING_ID, "Node color ranking");
        PropertyXml<String> startColor = new PropertyXml<String>(
                RankingXml.COLOR_RANKING_PROPERTY_COLOR1,
                "Start color",
                "Color of node with lowest value. In hexadecimal format: RRGGBB, e.g. FEF0D9",
                "FEF0D9");
        colorRanking.getProperties().add(startColor);

        PropertyXml<String> endColor = new PropertyXml<String>(
                RankingXml.COLOR_RANKING_PROPERTY_COLOR2,
                "End color",
                "Color of node with highest value. In hexadecimal format: RRGGBB, e.g. B30000",
                "B30000");
        colorRanking.getProperties().add(endColor);
        rankingXmlMap.put(colorRanking.getId(), colorRanking);

        // Node size ranking
        RankingXml sizeRanking = createRankingXml(RankingXml.SIZE_RANKING_ID, "Node size ranking");
        PropertyXml<Float> startSize = new PropertyXml<Float>(
                RankingXml.SIZE_RANKING_PROPERTY_SIZE1,
                "Start size",
                "Size of node with lowest value.",
                1f);
        sizeRanking.getProperties().add(startSize);

        PropertyXml<Float> endSize = new PropertyXml<Float>(
                RankingXml.SIZE_RANKING_PROPERTY_SIZE2,
                "End size",
                "Size of node with highest value.",
                5f);
        sizeRanking.getProperties().add(endSize);
        rankingXmlMap.put(sizeRanking.getId(), sizeRanking);
    }

    private RankingXml createRankingXml(String id, String name) {
        RankingXml rankingXml = new RankingXml();
        rankingXml.setId(id);
        rankingXml.setName(name);

        PropertyXml<String> attributeId = new PropertyXml<String>(
                RankingXml.RANKING_ATTRIBUTE_ID,
                "Attribute id",
                "Id of attribute which will be used to ranking. It has to be one of already calculated node attributes (see GEXF format)",
                Ranking.DEGREE_RANKING);
        rankingXml.addProperty(attributeId);

        return rankingXml;
    }

    private void initRankings() {
        List<RankingXml> all = new ArrayList<RankingXml>(rankingXmlMap.values());
        Collections.sort(all, new Comparator<RankingXml>() {
            @Override
            public int compare(RankingXml o1, RankingXml o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        rankings = new RankingsXml();
        rankings.getRankings().addAll(all);
    }
}
