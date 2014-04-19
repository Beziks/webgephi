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
    public List<GraphEntity> getPage(UserEntity owner, int pageSize, long page, boolean desc) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<GraphEntity> criteria = cb.createQuery(GraphEntity.class);
        Root<GraphEntity> root = criteria.from(GraphEntity.class);

        Predicate predicate = cb.equal(root.get(GraphEntity_.owner), owner);
        criteria.where(predicate);

        if(desc) {
            criteria.orderBy(cb.desc(root.get(GraphEntity_.id)));
        }

        long first = pageSize * page;

        return em.createQuery(criteria).setFirstResult((int) first).setMaxResults(pageSize).getResultList();
    }

    private long count() {
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        cq.select(qb.count(cq.from(GraphEntity.class)));
        return em.createQuery(cq).getSingleResult();
    }

    public long count(UserEntity owner) {
        CriteriaBuilder qb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        Root<GraphEntity> root = cq.from(GraphEntity.class);

        Predicate predicate = qb.equal(root.get(GraphEntity_.owner), owner);
        cq.where(predicate);

        cq.select(qb.count(root));
        return em.createQuery(cq).getSingleResult();
    }

    public long lastPage(UserEntity owner, int pageSize) {
        return (count(owner) - 1) / pageSize;
    }

    public GraphEntity get(Long id) {
        return em.find(GraphEntity.class, id);
    }

    public GraphEntity persist(GraphEntity e) {
        em.persist(e);
        return e;
    }
}
