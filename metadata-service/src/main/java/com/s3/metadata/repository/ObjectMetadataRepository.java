package com.s3.metadata.repository;

import com.s3.common.dto.response.ObjectMetadataResponseDTO;
import com.s3.metadata.model.ObjectMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ObjectMetadataRepository
        extends JpaRepository<ObjectMetadataEntity, UUID> {

    Optional<ObjectMetadataEntity> findByObjectId(String objectId);

    @Query("SELECT DISTINCT m FROM ObjectMetadataEntity m JOIN m.tags t WHERE t.tag = :tag")
    List<ObjectMetadataEntity> findByTag(String tag);


    @Query("""
        SELECT DISTINCT m
        FROM ObjectMetadataEntity m
        LEFT JOIN m.tags t
        WHERE m.ownerId = :ownerId
        AND (:fileName = '' OR m.bucketName = :bucketName)
        AND (:fileName = '' OR LOWER(m.fileName) LIKE CONCAT('%', :fileName, '%'))
        AND (:description = '' OR LOWER(m.description) LIKE CONCAT('%', :description, '%'))
        AND (:hasTags = false OR t.tag IN :tags)
        """)
    List<ObjectMetadataEntity> search(
            @Param("ownerId") String ownerId,
            @Param("bucketName") String bucketName,
            @Param("fileName") String fileName,
            @Param("description") String description,
            @Param("tags") List<String> tags,
            @Param("hasTags") boolean hasTags
    );
}
