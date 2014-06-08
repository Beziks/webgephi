package cz.cokrtvac.webgephi.api.model;

import cz.cokrtvac.webgephi.api.model.filter.FilterXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.api.model.ranking.RankingXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "function")
@XmlAccessorType(XmlAccessType.NONE)
public class GraphFunctionXml {
    private AbstractFunctionXml function;

    public GraphFunctionXml() {
    }

    public GraphFunctionXml(AbstractFunctionXml abstractFunction) {
        setFunction(abstractFunction);
    }

    @XmlElements({
            @XmlElement(name = "layout", type = LayoutXml.class),
            @XmlElement(name = "statistic", type = StatisticXml.class),
            @XmlElement(name = "ranking", type = RankingXml.class),
            @XmlElement(name = "filter", type = FilterXml.class)
    })
    public AbstractFunctionXml getFunction() {
        return function;
    }

    public void setFunction(AbstractFunctionXml function) {
        this.function = function;
    }
}
