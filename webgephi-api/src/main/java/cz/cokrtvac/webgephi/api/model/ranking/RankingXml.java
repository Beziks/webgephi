package cz.cokrtvac.webgephi.api.model.ranking;

import cz.cokrtvac.webgephi.api.model.AbstractFunction;
import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 3.6.13
 * Time: 18:20
 */
@XmlRootElement(name = "ranking")
public class RankingXml extends AbstractFunction {
    public static final String RANKING_ATTRIBUTE_ID = "attribute-id";

    public static final String COLOR_RANKING_ID = "color-ranking";
    public static final String COLOR_RANKING_PROPERTY_COLOR1 = "start-color";
    public static final String COLOR_RANKING_PROPERTY_COLOR2 = "end-color";

    public static final String SIZE_RANKING_ID = "size-ranking";
    public static final String SIZE_RANKING_PROPERTY_SIZE1 = "start-size";
    public static final String SIZE_RANKING_PROPERTY_SIZE2 = "end-size";

    @XmlElementRef
    private RESTServiceDiscovery restServiceDiscovery;
}
