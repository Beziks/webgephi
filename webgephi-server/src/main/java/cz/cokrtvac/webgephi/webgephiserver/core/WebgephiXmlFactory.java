package cz.cokrtvac.webgephi.webgephiserver.core;

import cz.cokrtvac.webgephi.api.model.PropertyXml;
import cz.cokrtvac.webgephi.api.model.graph.GraphDetailXml;
import cz.cokrtvac.webgephi.api.model.layout.LayoutXml;
import cz.cokrtvac.webgephi.api.model.statistic.StatisticXml;
import cz.cokrtvac.webgephi.api.model.user.UserXml;
import cz.cokrtvac.webgephi.api.util.StringUtil;
import cz.cokrtvac.webgephi.webgephiserver.core.gephi.statistics.StatisticsWrapper;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.GraphEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.User;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutProperty;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 30.1.14
 * Time: 22:33
 */
public class WebgephiXmlFactory {
    public static GraphDetailXml create(GraphEntity entity) {
        GraphDetailXml xml = new GraphDetailXml();
        xml.setId(entity.getId());
        xml.setName(entity.getName());
        xml.setCreated(entity.getCreated());
        xml.setOwner(entity.getOwner().getUsername());
        if (entity.getStatisticsReport() != null) {
            xml.restServiceDiscovery.addLink(URI.create(""), GraphDetailXml.STATISTICS_REPORT);
        }

        if (entity.getParent() != null) {
            GraphDetailXml xmlParent = new GraphDetailXml();
            xmlParent.setName(entity.getParent().getName());
            xmlParent.setCreated(entity.getParent().getCreated());
            xmlParent.setId(entity.getParent().getId());
            if (entity.getParent().getStatisticsReport() != null) {
                xmlParent.restServiceDiscovery.addLink(URI.create(""), GraphDetailXml.STATISTICS_REPORT);
            }
            xmlParent.setOwner(entity.getParent().getOwner().getUsername());
            xml.setParent(xmlParent);
        }
        return xml;
    }

    public static LayoutXml create(Layout l) {
        LayoutXml layoutXml = new LayoutXml();

        layoutXml.setName(l.getBuilder().getName());
        layoutXml.setId(StringUtil.uriSafe(layoutXml.getName()));

        for (LayoutProperty p : l.getProperties()) {
            layoutXml.addProperty(create(p));
        }

        return layoutXml;
    }

    public static StatisticXml create(StatisticsWrapper s) {
        StatisticXml statisticXml = new StatisticXml();

        statisticXml.setName(s.getName());
        statisticXml.setId(StringUtil.uriSafe(s.getName()));

        for (StatisticsWrapper.StatisticsProperty p : s.getProperties()) {
            statisticXml.addProperty(create(p));
        }

        return statisticXml;
    }

    public static UserXml create(User user) {
        UserXml x = new UserXml();
        x.setEmail(user.getEmail());
        x.setFirstName(user.getFirstName());
        x.setLastName(user.getLastName());
        x.setPassword(user.getPassword());
        x.setUsername(user.getUsername());
        return x;
    }

    public static PropertyXml create(LayoutProperty layoutProperty) {
        PropertyXml xml = new PropertyXml();
        xml.setName(layoutProperty.getProperty().getName());
        //xml.setType(layoutProperty.getProperty().getValueType());
        xml.setId(layoutProperty.getCanonicalName());
        xml.setDescription(layoutProperty.getProperty().getShortDescription());
        try {
            xml.setValue(layoutProperty.getProperty().getValue());
        } catch (IllegalAccessException e) {
            // TODO
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        }

        return xml;
    }

    public static PropertyXml create(StatisticsWrapper.StatisticsProperty statisticsProperty) {
        PropertyXml xml = new PropertyXml();
        xml.setName(statisticsProperty.getName());
        // xml.setType(statisticsProperty.getValueType());
        xml.setId(statisticsProperty.getName());
        // TODO xml.setDescription("not available");
        xml.setValue(statisticsProperty.getValue());

        return xml;
    }

    public static User fromXml(UserXml userXml) {
        User u = new User(
                userXml.getUsername(),
                userXml.getPassword(),
                userXml.getEmail(),
                userXml.getFirstName(),
                userXml.getLastName());
        return u;
    }
}
