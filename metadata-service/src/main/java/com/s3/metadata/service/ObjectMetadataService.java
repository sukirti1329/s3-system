package com.s3.metadata.service;

import com.s3.common.dto.ObjectMetadataDTO;
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
import java.util.UUID;


@Service
@Transactional
public class ObjectMetadataService {

    private static final Logger log = LoggingUtil.getLogger(ObjectMetadataService.class);

    private final ObjectMetadataRepository repository;
    private final ObjectMetadataMapper mapper;

    public ObjectMetadataService(
            ObjectMetadataRepository repository,
            ObjectMetadataMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // ---------------- CREATE ----------------
    public ObjectMetadataDTO create(ObjectMetadataDTO dto) {
        log.info("Creating metadata for objectId={}", dto.getObjectId());

        ObjectMetadataEntity entity = mapper.toEntity(dto);
        entity.setId(UUID.fromString(UUID.randomUUID().toString()));

        entity.setTags(mapper.mapStringsToTags(dto.getTags(), entity));

        ObjectMetadataEntity saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    // ---------------- GET ----------------
    @Transactional(readOnly = true)
    public ObjectMetadataDTO getByObjectId(String objectId) {
        ObjectMetadataEntity entity = repository
                .findByObjectId(objectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Metadata not found for objectId=" + objectId
                        )
                );
        return mapper.toDto(entity);
    }

    // ---------------- UPDATE ----------------
    public ObjectMetadataDTO update(String objectId, ObjectMetadataDTO dto) {
        log.info("Updating metadata for objectId={}", objectId);

        ObjectMetadataEntity existing = repository
                .findByObjectId(objectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Metadata not found for objectId=" + objectId
                        )
                );

        existing.setDescription(dto.getDescription());
        existing.setAccessLevel(dto.getAccessLevel());

        // Replace tags safely
        existing.getTags().clear();

        for (String tag : dto.getTags()) {
            ObjectTagEntity tagEntity = ObjectTagEntity.builder()
                    .tag(tag)
                    .metadata(existing)
                    .build();

            existing.getTags().add(tagEntity);
        }
        //existing.getTags().clear();
        //existing.getTags().addAll(mapper.mapStringsToTags(dto.getTags(), existing));

        return mapper.toDto(existing);
    }

    // ---------------- SEARCH ----------------
    @Transactional(readOnly = true)
    public List<ObjectMetadataDTO> searchByTag(String tag) {
        log.debug("Searching metadata by tag={}", tag);

        return repository.findByTag(tag)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
