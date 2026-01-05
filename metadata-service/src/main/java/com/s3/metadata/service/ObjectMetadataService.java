package com.s3.metadata.service;

import com.s3.common.dto.request.CreateObjectMetadataDTO;
import com.s3.common.dto.request.UpdateObjectMetadataDTO;
import com.s3.common.dto.response.ObjectMetadataResponseDTO;
import com.s3.common.exception.ResourceNotFoundException;
import com.s3.common.logging.LoggingUtil;
import com.s3.metadata.mapper.ObjectMetadataMapper;
import com.s3.metadata.model.ObjectMetadataEntity;
import com.s3.metadata.model.ObjectTagEntity;
import com.s3.metadata.repository.ObjectMetadataRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@Transactional
public class ObjectMetadataService {

    private static final Logger log =
            LoggingUtil.getLogger(ObjectMetadataService.class);

    private final ObjectMetadataRepository repository;
    private final ObjectMetadataMapper mapper;
    private final ObjectVersionService versionService;

    public ObjectMetadataService(
            ObjectMetadataRepository repository,
            ObjectMetadataMapper mapper,
            ObjectVersionService versionService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.versionService = versionService;
    }

    /* ===================== CREATE ===================== */

    public ObjectMetadataResponseDTO create(
            CreateObjectMetadataDTO dto,
            String ownerId
    ) {

        log.info("Creating metadata for objectId={}", dto.getObjectId());

        ObjectMetadataEntity entity = mapper.toEntity(dto);
        entity.setId(UUID.randomUUID());
        entity.setOwnerId(ownerId);
        entity.setAccessLevel(dto.getAccessLevel());
        entity.setActiveVersion(1);

        applyTags(entity, dto.getTags());

        ObjectMetadataEntity saved = repository.save(entity);

        // ✅ Correct call
        versionService.createInitialVersion(
                dto.getObjectId(),
                ownerId
        );

        return mapper.toResponse(saved);
    }

    /* ===================== UPDATE ===================== */

    public ObjectMetadataResponseDTO update(
            String objectId,
            UpdateObjectMetadataDTO dto
    ) {

        ObjectMetadataEntity entity = repository
                .findByObjectId(objectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Metadata not found for objectId=" + objectId
                        )
                );

        mapper.updateEntity(dto, entity);
        applyTags(entity, dto.getTags());

        ObjectMetadataEntity updated = repository.saveAndFlush(entity);

        if (Boolean.TRUE.equals(dto.getVersioningEnabled())) {
            versionService.createNewVersion(
                    objectId,
                    entity.getOwnerId()
            );
            entity.setActiveVersion(entity.getActiveVersion() + 1);
        }

        return mapper.toResponse(updated);
    }

    /* ===================== DELETE ===================== */

    @Transactional
    public void deleteByObjectId(String objectId) {

        ObjectMetadataEntity metadata = repository
                .findByObjectId(objectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Metadata not found for objectId=" + objectId
                        )
                );

        versionService.deleteByObjectId(objectId);
        repository.delete(metadata);
    }

    /* ===================== TAGS ===================== */

    private void applyTags(
            ObjectMetadataEntity entity,
            List<String> tags
    ) {
        if (tags == null) return;

        if (entity.getTags() == null) {
            entity.setTags(new ArrayList<>());
        } else {
            entity.getTags().clear();
        }

        for (String tag : tags) {
            entity.getTags().add(
                    ObjectTagEntity.builder()
                            .tag(tag)
                            .metadata(entity)
                            .build()
            );
        }
    }

    /* ===================== GET ===================== */

    @Transactional(readOnly = true)
    public ObjectMetadataResponseDTO getByObjectId(String objectId) {
        return repository.findByObjectId(objectId)
                .map(mapper::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Metadata not found for objectId=" + objectId
                        )
                );
    }
}

    /* ===================== CREATE ===================== */
