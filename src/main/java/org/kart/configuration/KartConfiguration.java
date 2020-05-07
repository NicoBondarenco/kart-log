package org.kart.configuration;

import org.kart.repository.KartLogJPARepository;
import org.kart.repository.KartLogRepository;
import org.kart.service.KartLogApplicationService;
import org.kart.service.KartLogService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class KartConfiguration {

    private static final EntityManager ENTITY_MANAGER;

    static {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("kart-persistence-unit");
        ENTITY_MANAGER = factory.createEntityManager();
    }

    private KartConfiguration() {
        super();
    }

    public static EntityManager entityManager() {
        return ENTITY_MANAGER;
    }

    public static KartLogRepository kartLogRepository() {
        return new KartLogJPARepository(entityManager());
    }

    public static KartLogService kartLogService() {
        return new KartLogApplicationService(kartLogRepository());
    }

}
