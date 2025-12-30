package com.s3.metadata.repository;

import com.s3.metadata.model.ObjectMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ObjectMetadataRepository
        extends JpaRepository<ObjectMetadataEntity, UUID> {

    Optional<ObjectMetadataEntity> findByObjectId(String objectId);

    @Query("SELECT DISTINCT m FROM ObjectMetadataEntity m JOIN m.tags t WHERE t.tag = :tag")
    List<ObjectMetadataEntity> findByTag(String tag);
}