package cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 27.1.14
 * Time: 11:39
 */
public class NoSuchRoleException extends Exception {
    private String invalidRole;

    public NoSuchRoleException() {
    }

    public NoSuchRoleException(String invalidRole){
        this(invalidRole, "This role is invalid: " + invalidRole);
    }

    public NoSuchRoleException(String invalidRole, String message) {
        super(message);
        this.invalidRole = invalidRole;
    }

    public String getInvalidRole() {
        return invalidRole;
    }
}
