package cz.cokrtvac.webgephi.webgephiserver.core.ejb;

import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.*;
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
        addDeaultGraph(user);
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






    public void addDeaultGraph(User user) {
        // Add graph
        GraphEntity e = new GraphEntity();
        e.setOwner(getUserEntity(user.getUsername()));
        e.setName("Missereables");
        e.setXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<gexf xmlns=\"http://www.gexf.net/1.2draft\" version=\"1.2\" xmlns:viz=\"http://www.gexf.net/1.2draft/viz\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd\">\n" +
                "  <meta lastmodifieddate=\"2013-06-04\">\n" +
                "    <creator>Gephi 0.8.1</creator>\n" +
                "    <description></description>\n" +
                "  </meta>\n" +
                "  <graph defaultedgetype=\"undirected\" mode=\"static\">\n" +
                "    <attributes class=\"node\" mode=\"static\">\n" +
                "      <attribute id=\"modularity_class\" title=\"Modularity Class\" type=\"integer\"></attribute>\n" +
                "    </attributes>\n" +
                "    <nodes>\n" +
                "      <node id=\"0\" label=\"Myriel\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"0\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"28.685715\"></viz:size>\n" +
                "        <viz:position x=\"-266.82776\" y=\"-299.6904\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"1\" label=\"Napoleon\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"0\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"-418.08344\" y=\"-446.8853\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"2\" label=\"MlleBaptistine\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"1\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"9.485714\"></viz:size>\n" +
                "        <viz:position x=\"-212.76357\" y=\"-245.29176\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"91\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"3\" label=\"MmeMagloire\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"1\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"9.485714\"></viz:size>\n" +
                "        <viz:position x=\"-242.82404\" y=\"-235.26283\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"91\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"4\" label=\"CountessDeLo\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"0\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"-379.30386\" y=\"-429.06424\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"5\" label=\"Geborand\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"0\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"-417.26337\" y=\"-406.03506\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"6\" label=\"Champtercier\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"0\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"-332.6012\" y=\"-485.16974\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"7\" label=\"Cravatte\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"0\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"-382.69568\" y=\"-475.09113\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"8\" label=\"Count\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"0\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"-320.384\" y=\"-387.17325\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"9\" label=\"OldMan\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"0\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"-344.39832\" y=\"-451.16772\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"10\" label=\"Labarre\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"1\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"-89.34107\" y=\"-234.56128\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"91\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"11\" label=\"Valjean\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"1\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"100.0\"></viz:size>\n" +
                "        <viz:position x=\"-87.93029\" y=\"6.8120565\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"91\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"12\" label=\"Marguerite\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"1\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"6.742859\"></viz:size>\n" +
                "        <viz:position x=\"-339.77908\" y=\"184.69139\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"91\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"13\" label=\"MmeDeR\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"1\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"-194.31313\" y=\"-178.55301\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"91\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"14\" label=\"Isabeau\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"1\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"-158.05168\" y=\"-201.99768\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"91\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"15\" label=\"Gervais\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"1\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"-127.701546\" y=\"-242.55057\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"91\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"16\" label=\"Tholomyes\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"2\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"25.942856\"></viz:size>\n" +
                "        <viz:position x=\"-385.2226\" y=\"393.5572\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"194\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"17\" label=\"Listolier\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"2\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"20.457146\"></viz:size>\n" +
                "        <viz:position x=\"-516.55884\" y=\"393.98975\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"194\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"18\" label=\"Fameuil\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"2\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"20.457146\"></viz:size>\n" +
                "        <viz:position x=\"-464.79382\" y=\"493.57944\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"194\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"19\" label=\"Blacheville\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"2\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"20.457146\"></viz:size>\n" +
                "        <viz:position x=\"-515.1624\" y=\"456.9891\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"194\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"20\" label=\"Favourite\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"2\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"20.457146\"></viz:size>\n" +
                "        <viz:position x=\"-408.12122\" y=\"464.5048\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"194\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"21\" label=\"Dahlia\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"2\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"20.457146\"></viz:size>\n" +
                "        <viz:position x=\"-456.44113\" y=\"425.13303\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"194\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"22\" label=\"Zephine\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"2\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"20.457146\"></viz:size>\n" +
                "        <viz:position x=\"-459.1107\" y=\"362.5133\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"194\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"23\" label=\"Fantine\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"2\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"42.4\"></viz:size>\n" +
                "        <viz:position x=\"-313.42786\" y=\"289.44803\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"194\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"24\" label=\"MmeThenardier\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"7\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"31.428574\"></viz:size>\n" +
                "        <viz:position x=\"4.6313396\" y=\"273.8517\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"194\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"25\" label=\"Thenardier\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"7\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"45.142853\"></viz:size>\n" +
                "        <viz:position x=\"82.80825\" y=\"203.1144\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"194\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"26\" label=\"Cosette\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"6\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"31.428574\"></viz:size>\n" +
                "        <viz:position x=\"78.64646\" y=\"31.512747\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"27\" label=\"Javert\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"7\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"47.88571\"></viz:size>\n" +
                "        <viz:position x=\"-81.46074\" y=\"204.20204\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"194\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"28\" label=\"Fauchelevent\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"4\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"12.228573\"></viz:size>\n" +
                "        <viz:position x=\"-225.73984\" y=\"-82.41631\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"194\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"29\" label=\"Bamatabois\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"3\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"23.2\"></viz:size>\n" +
                "        <viz:position x=\"-385.6842\" y=\"20.206686\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"30\" label=\"Perpetue\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"2\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"6.742859\"></viz:size>\n" +
                "        <viz:position x=\"-403.92447\" y=\"197.69823\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"194\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"31\" label=\"Simplice\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"2\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"12.228573\"></viz:size>\n" +
                "        <viz:position x=\"-281.4253\" y=\"158.45137\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"194\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"32\" label=\"Scaufflaire\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"1\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"-122.41348\" y=\"-210.37503\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"91\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"33\" label=\"Woman1\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"1\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"6.742859\"></viz:size>\n" +
                "        <viz:position x=\"-234.6001\" y=\"113.15067\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"91\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"34\" label=\"Judge\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"3\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"17.714287\"></viz:size>\n" +
                "        <viz:position x=\"-387.84915\" y=\"-58.7059\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"35\" label=\"Champmathieu\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"3\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"17.714287\"></viz:size>\n" +
                "        <viz:position x=\"-338.2307\" y=\"-87.48405\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"36\" label=\"Brevet\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"3\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"17.714287\"></viz:size>\n" +
                "        <viz:position x=\"-453.26874\" y=\"-58.94648\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"37\" label=\"Chenildieu\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"3\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"17.714287\"></viz:size>\n" +
                "        <viz:position x=\"-386.44904\" y=\"-140.05937\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"38\" label=\"Cochepaille\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"3\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"17.714287\"></viz:size>\n" +
                "        <viz:position x=\"-446.7876\" y=\"-123.38005\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"39\" label=\"Pontmercy\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"6\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"9.485714\"></viz:size>\n" +
                "        <viz:position x=\"336.49738\" y=\"269.55914\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"40\" label=\"Boulatruelle\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"7\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"29.187843\" y=\"460.13132\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"194\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"41\" label=\"Eponine\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"7\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"31.428574\"></viz:size>\n" +
                "        <viz:position x=\"238.36697\" y=\"210.00926\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"194\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"42\" label=\"Anzelma\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"7\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"9.485714\"></viz:size>\n" +
                "        <viz:position x=\"189.69513\" y=\"346.50662\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"194\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"43\" label=\"Woman2\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"6\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"9.485714\"></viz:size>\n" +
                "        <viz:position x=\"-187.00418\" y=\"145.02663\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"44\" label=\"MotherInnocent\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"4\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"6.742859\"></viz:size>\n" +
                "        <viz:position x=\"-252.99521\" y=\"-129.87549\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"194\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"45\" label=\"Gribier\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"4\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"-296.07935\" y=\"-163.11964\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"194\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"46\" label=\"Jondrette\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"5\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"550.3201\" y=\"-522.4031\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"91\" b=\"194\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"47\" label=\"MmeBurgon\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"5\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"6.742859\"></viz:size>\n" +
                "        <viz:position x=\"488.13535\" y=\"-356.8573\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"91\" b=\"194\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"48\" label=\"Gavroche\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"61.600006\"></viz:size>\n" +
                "        <viz:position x=\"387.89572\" y=\"-110.462326\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"49\" label=\"Gillenormand\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"6\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"20.457146\"></viz:size>\n" +
                "        <viz:position x=\"126.4831\" y=\"-68.10622\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"50\" label=\"Magnon\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"6\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"6.742859\"></viz:size>\n" +
                "        <viz:position x=\"127.07365\" y=\"113.05923\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"51\" label=\"MlleGillenormand\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"6\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"20.457146\"></viz:size>\n" +
                "        <viz:position x=\"162.63559\" y=\"-117.6565\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"52\" label=\"MmePontmercy\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"6\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"6.742859\"></viz:size>\n" +
                "        <viz:position x=\"353.66415\" y=\"205.89165\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"53\" label=\"MlleVaubois\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"6\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"165.43939\" y=\"-339.7736\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"54\" label=\"LtGillenormand\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"6\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"12.228573\"></viz:size>\n" +
                "        <viz:position x=\"137.69348\" y=\"-196.1069\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"55\" label=\"Marius\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"6\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"53.37143\"></viz:size>\n" +
                "        <viz:position x=\"206.44687\" y=\"13.805411\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"56\" label=\"BaronessT\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"6\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"6.742859\"></viz:size>\n" +
                "        <viz:position x=\"194.82993\" y=\"-224.78036\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"194\" g=\"91\" b=\"245\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"57\" label=\"Mabeuf\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"31.428574\"></viz:size>\n" +
                "        <viz:position x=\"597.6618\" y=\"-135.18481\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"58\" label=\"Enjolras\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"42.4\"></viz:size>\n" +
                "        <viz:position x=\"355.78366\" y=\"74.882454\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"59\" label=\"Combeferre\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"31.428574\"></viz:size>\n" +
                "        <viz:position x=\"515.2961\" y=\"46.167564\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"60\" label=\"Prouvaire\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"25.942856\"></viz:size>\n" +
                "        <viz:position x=\"614.29285\" y=\"69.3104\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"61\" label=\"Feuilly\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"31.428574\"></viz:size>\n" +
                "        <viz:position x=\"550.1917\" y=\"128.17537\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"62\" label=\"Courfeyrac\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"36.91429\"></viz:size>\n" +
                "        <viz:position x=\"436.17184\" y=\"12.7286825\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"63\" label=\"Bahorel\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"34.17143\"></viz:size>\n" +
                "        <viz:position x=\"602.55225\" y=\"-16.421427\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"64\" label=\"Bossuet\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"36.91429\"></viz:size>\n" +
                "        <viz:position x=\"455.81955\" y=\"115.45826\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"65\" label=\"Joly\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"34.17143\"></viz:size>\n" +
                "        <viz:position x=\"516.40784\" y=\"-47.242233\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"66\" label=\"Grantaire\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"28.685715\"></viz:size>\n" +
                "        <viz:position x=\"646.4313\" y=\"151.06331\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"67\" label=\"MotherPlutarch\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"4.0\"></viz:size>\n" +
                "        <viz:position x=\"668.9568\" y=\"-204.65488\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"68\" label=\"Gueulemer\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"7\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"28.685715\"></viz:size>\n" +
                "        <viz:position x=\"78.4799\" y=\"347.15146\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"194\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"69\" label=\"Babet\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"7\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"28.685715\"></viz:size>\n" +
                "        <viz:position x=\"150.35959\" y=\"298.50797\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"194\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"70\" label=\"Claquesous\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"7\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"28.685715\"></viz:size>\n" +
                "        <viz:position x=\"137.3717\" y=\"410.2809\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"194\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"71\" label=\"Montparnasse\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"7\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"25.942856\"></viz:size>\n" +
                "        <viz:position x=\"234.87747\" y=\"400.85983\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"194\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"72\" label=\"Toussaint\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"1\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"9.485714\"></viz:size>\n" +
                "        <viz:position x=\"40.942253\" y=\"-113.78272\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"245\" g=\"91\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"73\" label=\"Child1\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"6.742859\"></viz:size>\n" +
                "        <viz:position x=\"437.939\" y=\"-291.58234\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"74\" label=\"Child2\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"6.742859\"></viz:size>\n" +
                "        <viz:position x=\"466.04922\" y=\"-283.3606\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"75\" label=\"Brujon\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"7\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"20.457146\"></viz:size>\n" +
                "        <viz:position x=\"238.79364\" y=\"314.06345\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"194\"></viz:color>\n" +
                "      </node>\n" +
                "      <node id=\"76\" label=\"MmeHucheloup\">\n" +
                "        <attvalues>\n" +
                "          <attvalue for=\"modularity_class\" value=\"8\"></attvalue>\n" +
                "        </attvalues>\n" +
                "        <viz:size value=\"20.457146\"></viz:size>\n" +
                "        <viz:position x=\"712.18353\" y=\"-4.8131495\" z=\"0.0\"></viz:position>\n" +
                "        <viz:color r=\"91\" g=\"245\" b=\"91\"></viz:color>\n" +
                "      </node>\n" +
                "    </nodes>\n" +
                "    <edges>\n" +
                "      <edge id=\"0\" source=\"1\" target=\"0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"1\" source=\"2\" target=\"0\" weight=\"8.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"2\" source=\"3\" target=\"0\" weight=\"10.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"3\" source=\"3\" target=\"2\" weight=\"6.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"4\" source=\"4\" target=\"0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"5\" source=\"5\" target=\"0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"6\" source=\"6\" target=\"0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"7\" source=\"7\" target=\"0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"8\" source=\"8\" target=\"0\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"9\" source=\"9\" target=\"0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"13\" source=\"11\" target=\"0\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"11\" target=\"2\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"11\" source=\"11\" target=\"3\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"10\" source=\"11\" target=\"10\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"14\" source=\"12\" target=\"11\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"15\" source=\"13\" target=\"11\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"16\" source=\"14\" target=\"11\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"17\" source=\"15\" target=\"11\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"18\" source=\"17\" target=\"16\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"19\" source=\"18\" target=\"16\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"20\" source=\"18\" target=\"17\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"21\" source=\"19\" target=\"16\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"22\" source=\"19\" target=\"17\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"23\" source=\"19\" target=\"18\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"24\" source=\"20\" target=\"16\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"25\" source=\"20\" target=\"17\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"26\" source=\"20\" target=\"18\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"27\" source=\"20\" target=\"19\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"28\" source=\"21\" target=\"16\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"29\" source=\"21\" target=\"17\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"30\" source=\"21\" target=\"18\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"31\" source=\"21\" target=\"19\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"32\" source=\"21\" target=\"20\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"33\" source=\"22\" target=\"16\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"34\" source=\"22\" target=\"17\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"35\" source=\"22\" target=\"18\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"36\" source=\"22\" target=\"19\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"37\" source=\"22\" target=\"20\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"38\" source=\"22\" target=\"21\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"47\" source=\"23\" target=\"11\" weight=\"9.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"46\" source=\"23\" target=\"12\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"39\" source=\"23\" target=\"16\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"40\" source=\"23\" target=\"17\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"41\" source=\"23\" target=\"18\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"42\" source=\"23\" target=\"19\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"43\" source=\"23\" target=\"20\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"44\" source=\"23\" target=\"21\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"45\" source=\"23\" target=\"22\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"24\" target=\"11\" weight=\"7.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"48\" source=\"24\" target=\"23\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"52\" source=\"25\" target=\"11\" weight=\"12.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"51\" source=\"25\" target=\"23\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"50\" source=\"25\" target=\"24\" weight=\"13.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"26\" target=\"11\" weight=\"31.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"26\" target=\"16\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"53\" source=\"26\" target=\"24\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"56\" source=\"26\" target=\"25\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"57\" source=\"27\" target=\"11\" weight=\"17.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"58\" source=\"27\" target=\"23\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"27\" target=\"24\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"59\" source=\"27\" target=\"25\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"61\" source=\"27\" target=\"26\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"62\" source=\"28\" target=\"11\" weight=\"8.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"63\" source=\"28\" target=\"27\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"66\" source=\"29\" target=\"11\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"64\" source=\"29\" target=\"23\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"65\" source=\"29\" target=\"27\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"67\" source=\"30\" target=\"23\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"31\" target=\"11\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"31\" target=\"23\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"31\" target=\"27\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"68\" source=\"31\" target=\"30\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"72\" source=\"32\" target=\"11\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"73\" source=\"33\" target=\"11\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"74\" source=\"33\" target=\"27\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"75\" source=\"34\" target=\"11\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"76\" source=\"34\" target=\"29\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"77\" source=\"35\" target=\"11\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"35\" target=\"29\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"78\" source=\"35\" target=\"34\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"82\" source=\"36\" target=\"11\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"83\" source=\"36\" target=\"29\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"80\" source=\"36\" target=\"34\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"81\" source=\"36\" target=\"35\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"87\" source=\"37\" target=\"11\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"88\" source=\"37\" target=\"29\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"84\" source=\"37\" target=\"34\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"85\" source=\"37\" target=\"35\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"86\" source=\"37\" target=\"36\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"93\" source=\"38\" target=\"11\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"94\" source=\"38\" target=\"29\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"89\" source=\"38\" target=\"34\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"90\" source=\"38\" target=\"35\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"91\" source=\"38\" target=\"36\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"92\" source=\"38\" target=\"37\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"95\" source=\"39\" target=\"25\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"96\" source=\"40\" target=\"25\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"97\" source=\"41\" target=\"24\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"98\" source=\"41\" target=\"25\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"101\" source=\"42\" target=\"24\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"100\" source=\"42\" target=\"25\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"99\" source=\"42\" target=\"41\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"102\" source=\"43\" target=\"11\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"103\" source=\"43\" target=\"26\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"104\" source=\"43\" target=\"27\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"44\" target=\"11\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"105\" source=\"44\" target=\"28\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"107\" source=\"45\" target=\"28\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"108\" source=\"47\" target=\"46\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"112\" source=\"48\" target=\"11\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"110\" source=\"48\" target=\"25\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"111\" source=\"48\" target=\"27\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"109\" source=\"48\" target=\"47\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"49\" target=\"11\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"113\" source=\"49\" target=\"26\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"50\" target=\"24\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"115\" source=\"50\" target=\"49\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"119\" source=\"51\" target=\"11\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"118\" source=\"51\" target=\"26\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"117\" source=\"51\" target=\"49\" weight=\"9.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"52\" target=\"39\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"120\" source=\"52\" target=\"51\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"122\" source=\"53\" target=\"51\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"125\" source=\"54\" target=\"26\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"124\" source=\"54\" target=\"49\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"123\" source=\"54\" target=\"51\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"131\" source=\"55\" target=\"11\" weight=\"19.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"132\" source=\"55\" target=\"16\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"133\" source=\"55\" target=\"25\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"55\" target=\"26\" weight=\"21.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"128\" source=\"55\" target=\"39\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"134\" source=\"55\" target=\"41\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"135\" source=\"55\" target=\"48\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"127\" source=\"55\" target=\"49\" weight=\"12.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"126\" source=\"55\" target=\"51\" weight=\"6.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"129\" source=\"55\" target=\"54\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"136\" source=\"56\" target=\"49\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"137\" source=\"56\" target=\"55\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"57\" target=\"41\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"57\" target=\"48\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"138\" source=\"57\" target=\"55\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"145\" source=\"58\" target=\"11\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"58\" target=\"27\" weight=\"6.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"142\" source=\"58\" target=\"48\" weight=\"7.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"141\" source=\"58\" target=\"55\" weight=\"7.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"144\" source=\"58\" target=\"57\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"148\" source=\"59\" target=\"48\" weight=\"6.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"147\" source=\"59\" target=\"55\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"59\" target=\"57\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"146\" source=\"59\" target=\"58\" weight=\"15.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"150\" source=\"60\" target=\"48\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"151\" source=\"60\" target=\"58\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"152\" source=\"60\" target=\"59\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"153\" source=\"61\" target=\"48\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"158\" source=\"61\" target=\"55\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"157\" source=\"61\" target=\"57\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"154\" source=\"61\" target=\"58\" weight=\"6.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"156\" source=\"61\" target=\"59\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"155\" source=\"61\" target=\"60\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"164\" source=\"62\" target=\"41\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"162\" source=\"62\" target=\"48\" weight=\"7.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"159\" source=\"62\" target=\"55\" weight=\"9.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"62\" target=\"57\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"160\" source=\"62\" target=\"58\" weight=\"17.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"161\" source=\"62\" target=\"59\" weight=\"13.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"62\" target=\"60\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"165\" source=\"62\" target=\"61\" weight=\"6.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"63\" target=\"48\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"174\" source=\"63\" target=\"55\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"63\" target=\"57\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"63\" target=\"58\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"167\" source=\"63\" target=\"59\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"63\" target=\"60\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"172\" source=\"63\" target=\"61\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"169\" source=\"63\" target=\"62\" weight=\"6.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"184\" source=\"64\" target=\"11\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"64\" target=\"48\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"175\" source=\"64\" target=\"55\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"183\" source=\"64\" target=\"57\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"179\" source=\"64\" target=\"58\" weight=\"10.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"182\" source=\"64\" target=\"59\" weight=\"9.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"181\" source=\"64\" target=\"60\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"180\" source=\"64\" target=\"61\" weight=\"6.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"176\" source=\"64\" target=\"62\" weight=\"12.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"178\" source=\"64\" target=\"63\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"187\" source=\"65\" target=\"48\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"194\" source=\"65\" target=\"55\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"193\" source=\"65\" target=\"57\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"65\" target=\"58\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"192\" source=\"65\" target=\"59\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"65\" target=\"60\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"190\" source=\"65\" target=\"61\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"188\" source=\"65\" target=\"62\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"185\" source=\"65\" target=\"63\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"186\" source=\"65\" target=\"64\" weight=\"7.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"200\" source=\"66\" target=\"48\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"196\" source=\"66\" target=\"58\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"197\" source=\"66\" target=\"59\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"203\" source=\"66\" target=\"60\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"202\" source=\"66\" target=\"61\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"198\" source=\"66\" target=\"62\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"201\" source=\"66\" target=\"63\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"195\" source=\"66\" target=\"64\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"199\" source=\"66\" target=\"65\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"204\" source=\"67\" target=\"57\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"68\" target=\"11\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"68\" target=\"24\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"205\" source=\"68\" target=\"25\" weight=\"5.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"208\" source=\"68\" target=\"27\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"68\" target=\"41\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"209\" source=\"68\" target=\"48\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"213\" source=\"69\" target=\"11\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"214\" source=\"69\" target=\"24\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"211\" source=\"69\" target=\"25\" weight=\"6.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"69\" target=\"27\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"217\" source=\"69\" target=\"41\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"216\" source=\"69\" target=\"48\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"212\" source=\"69\" target=\"68\" weight=\"6.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"221\" source=\"70\" target=\"11\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"222\" source=\"70\" target=\"24\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"218\" source=\"70\" target=\"25\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"223\" source=\"70\" target=\"27\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"224\" source=\"70\" target=\"41\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"225\" source=\"70\" target=\"58\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"220\" source=\"70\" target=\"68\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"219\" source=\"70\" target=\"69\" weight=\"4.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"230\" source=\"71\" target=\"11\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"233\" source=\"71\" target=\"25\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"226\" source=\"71\" target=\"27\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"232\" source=\"71\" target=\"41\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"71\" target=\"48\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"228\" source=\"71\" target=\"68\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"227\" source=\"71\" target=\"69\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"229\" source=\"71\" target=\"70\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"236\" source=\"72\" target=\"11\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"234\" source=\"72\" target=\"26\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"235\" source=\"72\" target=\"27\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"237\" source=\"73\" target=\"48\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"238\" source=\"74\" target=\"48\" weight=\"2.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"239\" source=\"74\" target=\"73\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"242\" source=\"75\" target=\"25\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"244\" source=\"75\" target=\"41\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge source=\"75\" target=\"48\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"241\" source=\"75\" target=\"68\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"240\" source=\"75\" target=\"69\" weight=\"3.0\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"245\" source=\"75\" target=\"70\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"246\" source=\"75\" target=\"71\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"252\" source=\"76\" target=\"48\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"253\" source=\"76\" target=\"58\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"251\" source=\"76\" target=\"62\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"250\" source=\"76\" target=\"63\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"247\" source=\"76\" target=\"64\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"248\" source=\"76\" target=\"65\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "      <edge id=\"249\" source=\"76\" target=\"66\">\n" +
                "        <attvalues></attvalues>\n" +
                "      </edge>\n" +
                "    </edges>\n" +
                "  </graph>\n" +
                "</gexf>\n");
        em.persist(e);
    }

}
