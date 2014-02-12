package cz.cokrtvac.webgephi.api.model.user;

import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.persistence.Id;
import javax.xml.bind.annotation.*;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 21.1.14
 * Time: 17:53
 */

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.NONE)
public class UserXml {
    public UserXml() {
    }

    public UserXml(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Id
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;

    @XmlAttribute(required = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlElement
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlElement
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlElement
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @XmlElement
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @XmlElementRef
    private RESTServiceDiscovery restServiceDiscovery;
}
