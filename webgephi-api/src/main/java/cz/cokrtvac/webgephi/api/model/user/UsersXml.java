package cz.cokrtvac.webgephi.api.model.user;

import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "users")
@XmlType(propOrder = {"restServiceDiscovery", "users"})
@XmlAccessorType(XmlAccessType.NONE)
public class UsersXml {
    @XmlElement(name = "user")
    public List<UserXml> users;

    public List<UserXml> getUsers() {
        if (users == null) {
            users = new ArrayList<UserXml>();
        }
        return users;
    }

    public void setUsers(List<UserXml> users) {
        this.users = users;
    }

    @XmlElementRef
    private RESTServiceDiscovery restServiceDiscovery;

    public RESTServiceDiscovery getRestServiceDiscovery() {
        return restServiceDiscovery;
    }
}
