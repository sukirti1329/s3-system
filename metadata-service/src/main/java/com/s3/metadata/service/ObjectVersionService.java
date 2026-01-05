package com.s3.metadata.service;

import com.s3.common.dto.request.CreateObjectMetadataDTO;
import com.s3.common.dto.request.UpdateObjectMetadataDTO;
import com.s3.common.dto.response.ObjectVersionResponseDTO;
import com.s3.common.exception.ResourceNotFoundException;
import com.s3.common.logging.LoggingUtil;
import com.s3.metadata.mapper.ObjectVersionMapper;
import com.s3.metadata.model.ObjectVersionEntity;
import com.s3.metadata.repository.ObjectVersionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
    public void createInitialVersion(
            String objectId,
            String ownerId
    ) {

        log.info("Creating initial version (v1) for object [{}]", objectId);

        ObjectVersionEntity version = ObjectVersionEntity.builder()
                .id(UUID.randomUUID())
                .objectId(objectId)
                .ownerId(ownerId)
                .versionNumber(1)
                .isActive(true)
                .build();

        repository.save(version);

        log.info("Initial version [v1] created for object [{}]", objectId);
    }

    /* =====================================================
       CREATE NEW VERSION (OBJECT_UPDATED)
       ===================================================== */

    @Transactional
    public void createNewVersion(
            String objectId,
            String ownerId
    ) {

        log.info("Creating new version for object [{}]", objectId);

        repository.findByObjectIdAndIsActiveTrue(objectId)
                .ifPresent(active -> {
                    active.setActive(false);
                    repository.save(active);
                });

        int nextVersion = repository
                .findTopByObjectIdOrderByVersionNumberDesc(objectId)
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        ObjectVersionEntity version = ObjectVersionEntity.builder()
                .id(UUID.randomUUID())
                .objectId(objectId)
                .ownerId(ownerId)
                .versionNumber(nextVersion)
                .isActive(true)
                .build();

        repository.save(version);

        log.info("Version [v{}] created for object [{}]", nextVersion, objectId);
    }

    /* ===================== LIST ===================== */

    @Transactional(readOnly = true)
    public List<ObjectVersionResponseDTO> listVersions(String objectId) {
        return mapper.toResponseList(
                repository.findByObjectIdOrderByVersionNumberDesc(objectId)
        );
    }

    /* ===================== ROLLBACK ===================== */

    @Transactional
    public void rollback(String objectId, int versionNumber) {

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
    }

    /* ===================== DELETE ===================== */

    @Transactional
    public void deleteByObjectId(String objectId) {
        repository.deleteByObjectId(objectId);
    }

    @Transactional(readOnly = true)
    public ObjectVersionResponseDTO getActiveVersion(String objectId) {
        return repository.findByObjectIdAndIsActiveTrue(objectId)
                .map(mapper::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active version not found for objectId=" + objectId
                        )
                );
    }
}
