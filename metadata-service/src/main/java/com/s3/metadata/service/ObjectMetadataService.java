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

    private static final Logger log = LoggingUtil.getLogger(ObjectMetadataService.class);

    private final ObjectMetadataRepository repository;
    private final ObjectMetadataMapper mapper;

    public ObjectMetadataService(ObjectMetadataRepository repository, ObjectMetadataMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /* ===================== CREATE ===================== */

    public ObjectMetadataResponseDTO create(CreateObjectMetadataDTO dto, String ownerId) {
        {
            log.info("Creating metadata for objectId={} by user={}",
                    dto.getObjectId(), ownerId);

            ObjectMetadataEntity entity = mapper.toEntity(dto);
            entity.setId(UUID.randomUUID());
            entity.setOwnerId(ownerId);
            entity.setAccessLevel(dto.getAccessLevel());
            entity.setActiveVersion(1);
            applyTags(entity, dto.getTags());

            ObjectMetadataEntity saved = repository.save(entity);

            log.info("Metadata created for objectId={}", dto.getObjectId());
            return mapper.toResponse(saved);
        }
    }

    /* ===================== UPDATE ===================== */

    public ObjectMetadataResponseDTO update(String objectId, UpdateObjectMetadataDTO dto) {
        log.info("Updating metadata for objectId={}", objectId);

        ObjectMetadataEntity entity = repository
                .findByObjectId(objectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Metadata not found for objectId=" + objectId));

        mapper.updateEntity(dto, entity);
        applyTags(entity, dto.getTags());

        ObjectMetadataEntity updated = repository.saveAndFlush(entity);

        log.info("Metadata updated for objectId={}", objectId);
        return mapper.toResponse(updated);
    }


    private void applyTags(ObjectMetadataEntity entity, List<String> tags) {
        if (tags == null) return;
        if (entity.getTags() == null) {
            entity.setTags(new ArrayList<>());
        } else {
            entity.getTags().clear();
        }
        for (String tag : tags) {
            ObjectTagEntity tagEntity = ObjectTagEntity.builder()
                    .tag(tag)
                    .metadata(entity)
                    .build();
            entity.getTags().add(tagEntity);
        }
    }

    private void updateTags(ObjectMetadataEntity entity, List<String> tags) {
        if (tags == null) {
            return;
        }
        // Clear existing tags (orphanRemoval = true handles delete)
        entity.getTags().clear();
        // Re-create tags with proper back-reference
        for (String tag : tags) {
            ObjectTagEntity tagEntity = ObjectTagEntity.builder()
                    .tag(tag)
                    .metadata(entity)
                    .build();
            entity.getTags().add(tagEntity);
        }
    }
    /* ===================== GET ===================== */

    @Transactional(readOnly = true)
    public ObjectMetadataResponseDTO getByObjectId(String objectId) {
        log.info("Fetching metadata for objectId={}", objectId);

        return repository.findByObjectId(objectId)
                .map(mapper::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Metadata not found for objectId=" + objectId)
                );
    }
}
