package cz.cokrtvac.webgephi.webgephiserver.core.ejb;

import cz.cokrtvac.webgephi.webgephiserver.core.auth.oauth.OAuthManager;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.*;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.role.Role;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 9.6.13
 * Time: 17:52
 */
@Singleton
@Startup
public class InitSingleton {
    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    UserDAO userDAO;

    @Inject
    GraphDAO graphDAO;

    @Inject
    OAuthManager oAuthManager;

    @Inject
    OAuthDAO oAuthDAO;

    @PostConstruct
    private void insert() {
        if (!graphDAO.getAll().isEmpty()) {
            return;
        }

        // Init all roles
        for (Role r : Role.values()) {
            userDAO.getRole(r);
        }

        // Add users
        User admin = new User("admin", "password", "admin@admin.cz", "John", "Admin");
        try {
            userDAO.createAdminUser(admin);
            //addDeaultGraph(admin);
        } catch (ValidationException e) {
            log.error("Cannot createOrUpdate admin user", e);
        }

        OAuthConsumerEntity consumerEntity = new OAuthConsumerEntity();
        consumerEntity.setApplicationName("Webgephi Official Client");
        consumerEntity.setKey("client.webgephi.cz");
        consumerEntity.setConnectUri("http://127.0.0.1:8889/oauthclient");
        consumerEntity.setSecret("68c17d0d-5090-4b0e-bb2f-f4fe50d83704");

        UserEntity a = em.find(UserEntity.class, "admin");
        consumerEntity.setUser(a);
        em.persist(consumerEntity);

        OAuthAccessTokenEntity testToken = new OAuthAccessTokenEntity();
        Set<String> scopes = new HashSet<String>(Arrays.asList(new String[]{Role.PROFILE_READ.name(), Role.GRAPHS_READ.name(), Role.GRAPHS_WRITE.name()}));
        testToken.setScopes(scopes);
        testToken.setUser(userDAO.getUserEntity("admin"));
        testToken.setConsumer(consumerEntity);
        testToken.setToken("060a7ba5-d8c0-4969-a832-714b05f81440");
        testToken.setSecret("b51b92a5-c65c-492f-8ad7-e9dcb459ab2e");
        em.persist(testToken);


        User user = new User("user", "password", "user@user.cz", "Pepa", "User");
        try {
            userDAO.createRegularUser(user);
            //addDeaultGraph(user);
        } catch (ValidationException e) {
            log.error("Cannot createOrUpdate Pepa user", e);
        }

        // User used for testing ==========================================================
        User test = new User("testuser", "testuser", "test@user.com", "Test", "User");
        try {
            userDAO.createRegularUser(test);
            //addDeaultGraph(test);

        } catch (ValidationException e) {
            log.error("Cannot createOrUpdate Wrong user", e);
        }

        OAuthConsumerEntity testConsumerEntity = new OAuthConsumerEntity();
        testConsumerEntity.setApplicationName("Webgephi Test Client");
        testConsumerEntity.setKey("test.client");
        testConsumerEntity.setConnectUri("http://localhost.test");
        testConsumerEntity.setSecret("68c17d0d-test-4b0e-bb2f-f4fe50d83704");

        UserEntity testUserEntity = em.find(UserEntity.class, "testuser");
        testConsumerEntity.setUser(testUserEntity);
        em.persist(testConsumerEntity);

        OAuthAccessTokenEntity testUserToken = new OAuthAccessTokenEntity();
        Set<String> testScopes = new HashSet<String>(Arrays.asList(new String[]{Role.PROFILE_READ.name(), Role.GRAPHS_READ.name(), Role.GRAPHS_WRITE.name()}));
        testUserToken.setScopes(testScopes);
        testUserToken.setUser(testUserEntity);
        testUserToken.setConsumer(testConsumerEntity);
        testUserToken.setToken("060a7ba5-test-4969-a832-714b05f81440");
        testUserToken.setSecret("b51b92a5-test-492f-8ad7-e9dcb459ab2e");
        em.persist(testUserToken);

    }
}
