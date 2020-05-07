package org.kart.repository;

import org.kart.model.entity.KartLog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KartLogRepository {

    void insert(KartLog kartLog);

    List<KartLog> findAll();

    Optional<KartLog> findById(UUID id);
}
