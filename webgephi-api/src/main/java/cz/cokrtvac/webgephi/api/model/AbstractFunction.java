package cz.cokrtvac.webgephi.api.model;

import cz.cokrtvac.webgephi.api.util.Log;
import org.slf4j.Logger;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import java.util.ArrayList;
import java.util.List;

public class AbstractFunction {
    private Logger log = Log.get(getClass());

    private String id;
    private String name;
    private List<PropertyXml> properties;

    @XmlID
    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "property")
    public List<PropertyXml> getProperties() {
        if (properties == null) {
            properties = new ArrayList<PropertyXml>();
        }
        return properties;
    }

    public void setProperties(List<PropertyXml> properties) {
        this.properties = properties;
    }

    public void addProperty(PropertyXml prop) {
        getProperties().add(prop);
    }

    public PropertyXml<?> getProperty(String id) {
        for (PropertyXml<?> p : getProperties()) {
            if (id.equals(p.getId())) {
                return p;
            }
        }

        log.warn("Unknown property: " + id);
        return null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
