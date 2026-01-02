package com.s3.metadata.service;

import org.springframework.stereotype.Service;

import com.s3.common.dto.request.CreateObjectVersionDTO;
import com.s3.common.dto.response.ObjectVersionResponseDTO;
import com.s3.common.logging.LoggingUtil;
import com.s3.metadata.mapper.ObjectVersionMapper;
import com.s3.metadata.model.ObjectVersionEntity;
import com.s3.metadata.repository.ObjectVersionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjectVersionService {

    private static final Logger log = LoggingUtil.getLogger(ObjectVersionService.class);

    private final ObjectVersionRepository repository;
    private final ObjectVersionMapper mapper;

    /* ================= CREATE NEW VERSION ================= */

    @Transactional
    public void createVersion(String objectId, CreateObjectVersionDTO dto) {

        log.info("Creating new version for object [{}]", objectId);

        repository.findByObjectIdAndIsActiveTrue(objectId)
                .ifPresent(active -> {
                    active.setActive(false);
                    repository.save(active);
                });

        int nextVersion = repository
                .findByObjectIdOrderByVersionNumberDesc(objectId)
                .stream()
                .findFirst()
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        ObjectVersionEntity version = ObjectVersionEntity.builder()
                .objectId(objectId)
                .versionNumber(nextVersion)
                .checksum(dto.getChecksum())
                .storagePath(dto.getStoragePath())
                .isActive(true)
                .build();

        repository.save(version);

        log.info("Version [{}] created for object [{}]", nextVersion, objectId);
    }

    /* ================= LIST VERSIONS ================= */

    public List<ObjectVersionResponseDTO> listVersions(String objectId) {

        log.debug("Listing versions for object [{}]", objectId);

        return mapper.toResponseList(
                repository.findByObjectIdOrderByVersionNumberDesc(objectId)
        );
    }

    /* ================= ROLLBACK ================= */

    @Transactional
    public void rollback(String objectId, int versionNumber) {

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

        log.info("Rollback completed for object [{}]", objectId);
    }
}
