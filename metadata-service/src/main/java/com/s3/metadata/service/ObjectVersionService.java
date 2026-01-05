package com.s3.metadata.service;

import com.s3.common.dto.request.CreateObjectMetadataDTO;
import com.s3.common.dto.request.UpdateObjectMetadataDTO;
import com.s3.common.dto.response.ObjectVersionResponseDTO;
import com.s3.common.logging.LoggingUtil;
import com.s3.metadata.mapper.ObjectVersionMapper;
import com.s3.metadata.model.ObjectVersionEntity;
import com.s3.metadata.repository.ObjectVersionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjectVersionService {

    private static final Logger log =
            LoggingUtil.getLogger(ObjectVersionService.class);

    private final ObjectVersionRepository repository;
    private final ObjectVersionMapper mapper;

    /* =====================================================
       CREATE INITIAL VERSION (OBJECT_CREATED)
       ===================================================== */

    @Transactional
    public void createInitialVersion(CreateObjectMetadataDTO metadata) {
        log.info("Creating initial version (v1) for object [{}]", metadata.getObjectId());
        ObjectVersionEntity version = ObjectVersionEntity.builder()
                .objectId(metadata.getObjectId())
                .versionNumber(1)
                //.checksum(metadata.getChecksum())
               // .storagePath(metadata.getStoragePath())
                .isActive(true)
                .build();

        repository.save(version);
        log.info("Initial version [v1] created for object [{}]", metadata.getObjectId()
        );
    }

    /* =====================================================
       CREATE NEW VERSION (OBJECT_UPDATED)
       ===================================================== */

    @Transactional
    public void createNewVersion(String objectId, UpdateObjectMetadataDTO update
    ) {
        log.info("Creating new version for object [{}]", objectId);
        //  Deactivate current active version
        repository.findByObjectIdAndIsActiveTrue(objectId)
                .ifPresent(active -> {
                    active.setActive(false);
                    repository.save(active);
                });

        //  Determine next version number
        int nextVersion = repository
                .findTopByObjectIdOrderByVersionNumberDesc(objectId)
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        // Create new version
        ObjectVersionEntity version = ObjectVersionEntity.builder()
                .objectId(objectId)
                .versionNumber(nextVersion)
               // .checksum(update.getChecksum())
              //  .storagePath(update.getStoragePath())
                .isActive(true)
                .build();

        repository.save(version);
        log.info(
                "Version [v{}] created for object [{}]",
                nextVersion,
                objectId
        );
    }

    /* =====================================================
       LIST VERSIONS
       ===================================================== */

    @Transactional(readOnly = true)
    public List<ObjectVersionResponseDTO> listVersions(
            String objectId
    ) {

        log.debug("Listing versions for object [{}]", objectId);

        return mapper.toResponseList(
                repository.findByObjectIdOrderByVersionNumberDesc(objectId)
        );
    }

    /* =====================================================
       ROLLBACK
       ===================================================== */

    @Transactional
    public void rollback(
            String objectId,
            int versionNumber
    ) {

        log.warn("Rolling back object [{}] to version [{}]", objectId, versionNumber);
        ObjectVersionEntity target = repository
                .findByObjectIdAndVersionNumber(objectId, versionNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("Version not found")
                );

        repository.findByObjectIdAndIsActiveTrue(objectId)
                .ifPresent(active -> {
                    active.setActive(false);
                    repository.save(active);
                });

        target.setActive(true);
        repository.save(target);

        log.info(
                "Rollback completed for object [{}] to version [{}]",
                objectId,
                versionNumber
        );
    }
}
