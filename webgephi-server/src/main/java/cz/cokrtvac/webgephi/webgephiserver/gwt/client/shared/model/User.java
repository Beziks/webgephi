package cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Portable
@Bindable
@XmlRootElement(name = "user")
public class User {
    private static final String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
    private static final String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)*";
    private static final String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

    private static final String EMAIL_PATTERN = "^" + ATOM + "+(\\." + ATOM + "+)*@"
            + DOMAIN
            + "|"
            + IP_DOMAIN
            + ")$";

    public User() {
    }

    public User(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Id
    @NotNull
    @Size(min = 3, max = 32, message = "Username has to be 3-32 characters long")
    @Pattern(regexp = "[A-Za-z0-9]+", message = "Only basic letters and numbers are allowed (aA..zZ,0..9)")
    private String username;

    @NotNull
    @Size(min = 5, max = 32, message = "Password has to be 5-32 characters long")
    private String password;

    @NotNull
    @Pattern(regexp = EMAIL_PATTERN, message = "Invalid email format")
    private String email;

    @NotNull
    @Size(min = 1, max = 32, message = "FirstName has to be 1-32 characters long")
    private String firstName;

    @NotNull
    @Size(min = 1, max = 32, message = "LastName has to be 1-32 characters long")
    private String lastName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

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
}
