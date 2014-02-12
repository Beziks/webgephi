package cz.cokrtvac.webgephi.api.model.statistic;

import cz.cokrtvac.webgephi.api.model.AbstractFunction;
import cz.cokrtvac.webgephi.api.model.HasId;
import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 3.6.13
 * Time: 18:20
 */
@XmlRootElement(name = "statistic")
public class StatisticXml extends AbstractFunction implements HasId {

    @XmlElementRef
    private RESTServiceDiscovery restServiceDiscovery;
}
