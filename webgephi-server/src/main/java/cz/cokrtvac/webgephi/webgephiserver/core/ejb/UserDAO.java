package cz.cokrtvac.webgephi.webgephiserver.core.ejb;

import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.OAuthConsumerEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.User;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.UserEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.ValidationException;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 19:36
 */
@Stateless
public class UserDAO {
    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private IdentityManager identityManager;

    @Inject
    private Validator validator;

    @Resource
    private SessionContext sessionContext;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<User> getAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> criteria = cb.createQuery(UserEntity.class);
        Root<UserEntity> root = criteria.from(UserEntity.class);

        List<User> all = new ArrayList<User>();

        for (UserEntity e : em.createQuery(criteria).getResultList()) {
            all.add(get(e.getUsername()));
        }

        return all;
    }

    public User get(String username) {
        UserEntity e = em.find(UserEntity.class, username);
        if (e == null) {
            return null;
        }
        org.picketlink.idm.model.User identityUser = identityManager.getUser(username);
        if (e == null) {
            log.error("Picket link user " + username + " does not exist, it should not happen");
            return null;
        }

        User u = new User(identityUser.getLoginName(), null, identityUser.getEmail(), identityUser.getFirstName(), identityUser.getLastName());
        return u;
    }

    public UserEntity getUserEntity(String username) {
        return em.find(UserEntity.class, username);
    }

    public OAuthConsumerEntity getClientApp(String username) {
        return em.find(UserEntity.class, username).getClientAppEntity();
    }

    public org.picketlink.idm.model.User createRegularUser(User user) throws ValidationException {
        if (getUserEntity(user.getUsername()) != null || identityManager.getUser(user.getUsername()) != null) {
            throw new ValidationException("User " + user.getUsername() + " is not valid", "Username " + user.getUsername() + " is already taken.");
        }

        if(user.getUsername().equalsIgnoreCase("logged")){
            throw new ValidationException(user.getUsername() + " is not valid username. 'Logged' is a reserved word.");
        }

        // Validate
        Set<ConstraintViolation<User>> results = validator.validate(user);
        List<String> reasons = new ArrayList<String>();

        for (ConstraintViolation<User> v : results) {
            log.warn(v.getMessage());
            reasons.add(v.getMessage());
        }

        if (!results.isEmpty()) {
            String m = "User " + user.getUsername() + " is not valid";
            log.error(m);
            throw new ValidationException(m, reasons);
        }

        // Save entity (username only)
        em.persist(new UserEntity(user.getUsername()));
        em.flush();

        // Save to picketlink
        org.picketlink.idm.model.User u = new SimpleUser(user.getUsername());
        u.setEmail(user.getEmail());
        u.setFirstName(user.getFirstName());
        u.setLastName(user.getLastName());
        identityManager.add(u);
        final Password pass = new Password(user.getPassword());
        identityManager.updateCredential(u, pass);

        // Set permissions (roles)
        identityManager.grantRole(u, getRole(cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role.USER));
        identityManager.grantRole(u, getRole(cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role.PROFILE_READ));
        identityManager.grantRole(u, getRole(cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role.PROFILE_WRITE));
        identityManager.grantRole(u, getRole(cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role.GRAPHS_READ));
        identityManager.grantRole(u, getRole(cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role.GRAPHS_WRITE));
        return u;
    }

    public org.picketlink.idm.model.User createAdminUser(User user) throws ValidationException {
        org.picketlink.idm.model.User u = createRegularUser(user);
        identityManager.grantRole(u, getRole(cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role.ADMIN));
        return u;
    }

    public void update(User user) throws ValidationException {
        // Validate
        Set<ConstraintViolation<User>> results = validator.validate(user);
        List<String> reasons = new ArrayList<String>();

        for (ConstraintViolation<User> v : results) {
            log.warn(v.getMessage());
            reasons.add(v.getMessage());
        }

        if (!results.isEmpty()) {
            String m = "User " + user.getUsername() + " is not valid";
            log.error(m);
            throw new ValidationException(m, reasons);
        }

        // Save to picketlink
        org.picketlink.idm.model.User u = identityManager.getUser(user.getUsername());
        u.setEmail(user.getEmail());
        u.setFirstName(user.getFirstName());
        u.setLastName(user.getLastName());
        identityManager.update(u);
        final Password pass = new Password(user.getPassword());
        identityManager.updateCredential(u, pass);
    }

    // Client app
    public OAuthConsumerEntity createOrUpdate(OAuthConsumerEntity clientAppEntity, String username) throws ValidationException {
        UserEntity ue = getUserEntity(username);

        // Validate
        Set<ConstraintViolation<OAuthConsumerEntity>> results = validator.validate(clientAppEntity);
        List<String> reasons = new ArrayList<String>();

        for (ConstraintViolation<OAuthConsumerEntity> v : results) {
            log.warn(v.getMessage());
            reasons.add(v.getMessage());
        }

        if (!results.isEmpty()) {
            String m = "Client application " + clientAppEntity.getKey() + " is not valid, it cannot be saved...";
            log.error(m);
            throw new ValidationException(m, reasons);
        }

        OAuthConsumerEntity e = ue.getClientAppEntity();
        if (e == null) {
            log.info("Creating client app entity");
            e = clientAppEntity;
            em.persist(e);
        } else {
            log.info("Updating client app entity");
            e = em.merge(clientAppEntity);
        }

        e.setUser(ue);
        return e;
    }

    // ROLES ------------------------------------
    public Role getRole(cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role role) {
        Role r = identityManager.getRole(role.name());
        if (r == null) {
            identityManager.add(new SimpleRole(role.name()));
            r = identityManager.getRole(role.name());
        }
        return r;
    }
}
