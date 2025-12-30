package com.s3.metadata.repository;

import com.s3.metadata.model.ObjectVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ObjectVersionRepository
        extends JpaRepository<ObjectVersionEntity, UUID> {

    Optional<ObjectVersionEntity> findByObjectIdAndIsActiveTrue(String objectId);

    int countByObjectId(String objectId);
}
