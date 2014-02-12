package cz.cokrtvac.webgephi.api.model;

import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.*;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 3.6.13
 * Time: 18:23
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class PropertyXml<T> implements HasId {
    private String id;
    private Class<?> type;
    private String name;
    private String description;
    private T value;

    @XmlID
    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(required = true)
    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    @XmlElement(required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(required = true)
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "PropertyXml{" + "type=" + type + ", name='" + name + '\'' + ", description='" + description + '\'' + ", value=" + value + '}';
    }

    @XmlElementRef
    private RESTServiceDiscovery restServiceDiscovery;
}
