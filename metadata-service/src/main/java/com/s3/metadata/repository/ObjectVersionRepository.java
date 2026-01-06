package com.s3.metadata.repository;

import com.s3.metadata.model.ObjectVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ObjectVersionRepository
        extends JpaRepository<ObjectVersionEntity, UUID> {

    Optional<ObjectVersionEntity> findByObjectIdAndIsActiveTrue(String objectId);
    int countByObjectId(String objectId);

    Optional<ObjectVersionEntity> findTopByObjectIdOrderByVersionNumberDesc(String objectId);
    List<ObjectVersionEntity> findByObjectIdOrderByVersionNumberDesc(String objectId);
    Optional<ObjectVersionEntity> findByObjectIdAndVersionNumber(String objectId, int versionNumber);
    void deleteByObjectId(String objectId);

//    @Modifying
//    @Query("UPDATE ObjectVersionEntity v SET v.versioningEnabled = :enabled WHERE v.object.id = :objectId")
//    int updateVersioningEnabled(@Param("objectId") String objectId, @Param("enabled") boolean enabled
//    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE ObjectVersionEntity v SET v.versioningEnabled = :enabled WHERE v.objectId = :objectId")
    int updateVersioningEnabled(@Param("objectId") String objectId, @Param("enabled") boolean enabled);
}
