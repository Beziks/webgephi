package cz.cokrtvac.webgephi.webgephiserver.core.rest;

import cz.cokrtvac.webgephi.api.model.WebgephiWebException;
import cz.cokrtvac.webgephi.api.model.user.UserXml;
import cz.cokrtvac.webgephi.api.model.user.UsersXml;
import cz.cokrtvac.webgephi.webgephiserver.core.WebgephiXmlFactory;
import cz.cokrtvac.webgephi.webgephiserver.core.ejb.GraphDAO;
import cz.cokrtvac.webgephi.webgephiserver.core.ejb.UserDAO;
import cz.cokrtvac.webgephi.webgephiserver.core.rest.decorator.AddLinksEnhanced;
import cz.cokrtvac.webgephi.webgephiserver.core.rest.interceptor.WebException;
import cz.cokrtvac.webgephi.webgephiserver.core.util.security_annotation.Secure;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.User;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role;
import org.jboss.resteasy.links.LinkResource;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

/**
 * Resources over User
 */
@Path("/users")
@Stateless
@WebException
@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
public class UserResourceRESTService {
    @Resource
    private SessionContext sessionContext;

    @Inject
    private Logger log;

    @Inject
    private GraphDAO graphDAO;

    @Inject
    private UserDAO userDAO;

    @Secure(Role.ADMIN)
    @AddLinksEnhanced
    @LinkResource
    @GET
    public UsersXml getAllUsers(@Context HttpServletRequest req) {
        UsersXml us = new UsersXml();
        for (User e : userDAO.getAll()) {
            e.setPassword(null);
            us.getUsers().add(WebgephiXmlFactory.create(e));
        }
        return us;
    }

    @Secure(value = Role.PROFILE_READ, owner = "#{arg1}")
    @AddLinksEnhanced
    @LinkResource(value = UserXml.class)
    @GET
    @Path("{user}")
    public UserXml getUser(@Context HttpServletRequest req, @PathParam("user") String user) {
        User u = getUser(user);
        u.setPassword(null);
        return WebgephiXmlFactory.create(u);
    }

    /**
     * Creates a new user
     */
    @AddLinksEnhanced
    @LinkResource(value = UsersXml.class)
    @POST
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    public UserXml addUser(@Context HttpServletRequest req, UserXml user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new WebgephiWebException(Status.CONFLICT, "Password has to be set");
        }

        try {
            userDAO.createRegularUser(WebgephiXmlFactory.fromXml(user));
        } catch (ValidationException ve) {
            StringBuilder sb = null;
            if (ve.getReasons() != null && !ve.getReasons().isEmpty()) {
                sb = new StringBuilder();
                for (String s : ve.getReasons()) {
                    sb.append(s).append("\n");
                }
            }
            if (sb != null) {
                throw new WebgephiWebException(Status.CONFLICT, "User is invalid", sb.toString());
            } else {
                throw new WebgephiWebException(Status.CONFLICT, "User is invalid");
            }
        } catch (Exception e) {
            new WebgephiWebException(Status.CONFLICT, "User could not be created", e);
        }

        return WebgephiXmlFactory.create(getUser(user.getUsername()));
    }

    /**
     * Creates a new user
     */
    @Secure(value = Role.PROFILE_WRITE, owner = "#{arg2}")
    @PUT
    @Path("{username}")
    @AddLinksEnhanced
    @LinkResource(value = UserXml.class, rel = "update")
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    public UserXml updateUser(@Context HttpServletRequest req, UserXml user, @PathParam("username") String username) {
        User u = getUser(username);
        if (!u.getUsername().equals(username)) {
            throw new WebgephiWebException(Status.CONFLICT, "Username cannot be changed", "Username in uri has to be the same as in content");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new WebgephiWebException(Status.CONFLICT, "Password has to be set");
        }

        try {
            userDAO.update(WebgephiXmlFactory.fromXml(user));
        } catch (ValidationException ve) {
            StringBuilder sb = null;
            if (ve.getReasons() != null && !ve.getReasons().isEmpty()) {
                sb = new StringBuilder();
                for (String s : ve.getReasons()) {
                    sb.append(s).append("\n");
                }
            }
            if (sb != null) {
                throw new WebgephiWebException(Status.CONFLICT, "User is invalid", sb.toString());
            } else {
                throw new WebgephiWebException(Status.CONFLICT, "User is invalid");
            }
        } catch (Exception e) {
            new WebgephiWebException(Status.CONFLICT, "User could not be created", e);
        }

        user.setPassword(null);
        return user;
    }

    private User getUser(String user) {
        User u = userDAO.get(user);
        if (u == null) {
            throw new WebgephiWebException(Status.NOT_FOUND, "User " + user + " does not exist");
        }
        return u;
    }
}
