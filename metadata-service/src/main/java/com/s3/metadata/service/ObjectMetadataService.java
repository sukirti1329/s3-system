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

import java.util.List;

@Service
@Transactional
public class ObjectMetadataService {

    private static final Logger log = LoggingUtil.getLogger(ObjectMetadataService.class);

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
    @Transactional
    public ObjectMetadataResponseDTO create(
            CreateObjectMetadataDTO dto,
            String ownerId
    ) {
        log.info("Creating metadata for objectId={}", dto.getObjectId());

        //  MapStruct creates entity using no-arg constructor + setters
        ObjectMetadataEntity entity = mapper.toEntity(dto);
        entity.setOwnerId(ownerId);
        entity.setAccessLevel(dto.getAccessLevel());
        entity.setActiveVersion(1);

        //  Apply tags BEFORE saving
        applyTags(entity, dto.getTags());

        log.info("Entity before save - tags count: {}", entity.getTags().size());

        ObjectMetadataEntity saved = repository.save(entity);

        log.info("Entity after save - tags count: {}", saved.getTags().size());

        versionService.createInitialVersion(
                dto.getObjectId(),
                ownerId
        );

        return mapper.toResponse(saved);
    }

    /* ===================== UPDATE ===================== */
    @Transactional
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

        // Update tags if provided
        if (dto.getTags() != null) {
            applyTags(entity, dto.getTags());
        }

        if (Boolean.TRUE.equals(dto.isVersioningEnabled())) {
            versionService.createNewVersion(
                    objectId,
                    entity.getOwnerId()
            );
            entity.setActiveVersion(entity.getActiveVersion() + 1);
        }

        ObjectMetadataEntity updated = repository.save(entity);

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
        log.info("Applying tags - current count: {}, new tags: {}",
                entity.getTags() != null ? entity.getTags().size() : 0,
                tags);

        // ✅ Clear existing tags properly using helper method
        entity.clearTags();

        if (tags == null || tags.isEmpty()) {
            log.info("No tags to apply");
            return;
        }

        // ✅ Add new tags using helper method
        for (String tagValue : tags) {
            ObjectTagEntity tag = ObjectTagEntity.builder()
                    .tag(tagValue)
                    .build();
            entity.addTag(tag);  // This sets both sides of the relationship
            log.info("Added tag: {} to entity", tagValue);
        }

        log.info("Final tags count after apply: {}", entity.getTags().size());
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