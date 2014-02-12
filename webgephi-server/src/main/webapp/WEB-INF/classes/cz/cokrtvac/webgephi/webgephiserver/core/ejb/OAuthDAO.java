package cz.cokrtvac.webgephi.webgephiserver.core.ejb;

import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.*;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 26.1.14
 * Time: 12:41
 */
@Stateless
public class OAuthDAO {
    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private UserDAO userDAO;

    public OAuthConsumerEntity getConsumerEntity(String key) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<OAuthConsumerEntity> criteria = cb.createQuery(OAuthConsumerEntity.class);
        Root<OAuthConsumerEntity> root = criteria.from(OAuthConsumerEntity.class);

        Predicate predicate = cb.equal(root.get(OAuthConsumerEntity_.key), key);
        criteria.where(predicate);

        return em.createQuery(criteria).getSingleResult();
    }

    public OAuthRequestTokenEntity getRequestTokenEntity(String token) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<OAuthRequestTokenEntity> criteria = cb.createQuery(OAuthRequestTokenEntity.class);
        Root<OAuthRequestTokenEntity> root = criteria.from(OAuthRequestTokenEntity.class);

        Predicate predicate = cb.equal(root.get(OAuthRequestTokenEntity_.token), token);
        criteria.where(predicate);

        log.info("Searching request token: " + token);
        return em.createQuery(criteria).getSingleResult();
    }

    public OAuthAccessTokenEntity getAccessTokenEntity(String token) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<OAuthAccessTokenEntity> criteria = cb.createQuery(OAuthAccessTokenEntity.class);
        Root<OAuthAccessTokenEntity> root = criteria.from(OAuthAccessTokenEntity.class);

        Predicate predicate = cb.equal(root.get(OAuthAccessTokenEntity_.token), token);
        criteria.where(predicate);

        return em.createQuery(criteria).getSingleResult();
    }

    //---
    public OAuthRequestTokenEntity getRequestTokenWithConsumer(String requestToken) {
        OAuthRequestTokenEntity e = getRequestTokenEntity(requestToken);
        // Lazy loading
        e.getConsumer();
        return e;
    }

    public UserEntity getUserWithAuthorizedAccessTokens(String username) {
        UserEntity e = userDAO.getUserEntity(username);
        for (OAuthAccessTokenEntity token : e.getAccessTokens()) {
            token.getConsumer();
        }
        return e;
    }

    public void deleteAccessToken(String token) {
        OAuthAccessTokenEntity e = getAccessTokenEntity(token);
        em.remove(e);
    }
}
