package cz.cokrtvac.webgephi.api.model;

import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "function")
@XmlAccessorType(XmlAccessType.NONE)
public class GraphFunction {
    private AbstractFunction function;

    @XmlElements({
            @XmlElement(name = "layout", type = LayoutXml.class),
            @XmlElement(name = "statistic", type = StatisticXml.class)
    })
    public AbstractFunction getFunction() {
        return function;
    }

    public void setFunction(AbstractFunction function) {
        this.function = function;
    }
}
