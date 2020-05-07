package org.kart.repository;

import org.kart.exception.KartLogException;
import org.kart.model.entity.KartLog;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.ofNullable;

public class KartLogJPARepository implements KartLogRepository {

    private final EntityManager entityManager;

    public KartLogJPARepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void insert(KartLog kartLog) {
        entityManager.getTransaction().begin();
        try {
            entityManager.persist(kartLog);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new KartLogException("Erro ao salvar dados do log kart", e);
        }
    }

    @Override
    public List<KartLog> findAll() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<KartLog> query = builder.createQuery(KartLog.class);
        Root<KartLog> rootQuery = query.from(KartLog.class);
        query.select(rootQuery);
        TypedQuery<KartLog> result = entityManager.createQuery(query);
        return result.getResultList();
    }

    @Override
    public Optional<KartLog> findById(UUID id) {
        return ofNullable(entityManager.find(KartLog.class, id));
    }

}
