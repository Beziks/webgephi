package cz.cokrtvac.webgephi.webgephiserver.core.ejb;

import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.GraphEntity;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.GraphEntity_;
import cz.cokrtvac.webgephi.webgephiserver.gwt.client.shared.model.UserEntity;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 4.6.13
 * Time: 19:36
 */
@Stateless
public class GraphDAO {
    @Inject
    private EntityManager em;

    public List<GraphEntity> getAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GraphEntity> criteria = cb.createQuery(GraphEntity.class);

        Root<GraphEntity> root = criteria.from(GraphEntity.class);

        return em.createQuery(criteria).getResultList();
    }

    /**
     * @param pageSize
     * @param page     - indexing from 0
     * @return
     */
    public List<GraphEntity> getPage(UserEntity owner, int pageSize, long page) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<GraphEntity> criteria = cb.createQuery(GraphEntity.class);
        Root<GraphEntity> root = criteria.from(GraphEntity.class);

        Predicate predicate = cb.equal(root.get(GraphEntity_.owner), owner);
        criteria.where(predicate);

        long first = pageSize * page;

        return em.createQuery(criteria).setFirstResult((int) first).setMaxResults(pageSize).getResultList();
    }

    public long count() {
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        cq.select(qb.count(cq.from(GraphEntity.class)));
        return em.createQuery(cq).getSingleResult();
    }

    public long lastPage(int pageSize) {
        return (count() - 1) / pageSize;
    }

    public GraphEntity get(Long id) {
        return em.find(GraphEntity.class, id);
    }

    public GraphEntity persist(GraphEntity e) {
        em.persist(e);
        return e;
    }
}
