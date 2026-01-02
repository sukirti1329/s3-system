package com.s3.metadata.mapper;

import com.s3.common.dto.request.CreateObjectMetadataDTO;
import com.s3.common.dto.response.ObjectMetadataResponseDTO;
import com.s3.metadata.model.ObjectMetadataEntity;
import com.s3.metadata.model.ObjectTagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.s3.common.dto.request.UpdateObjectMetadataDTO;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = false))
public interface ObjectMetadataMapper {

    /* ===================== CREATE ===================== */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "activeVersion", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    ObjectMetadataEntity toEntity(CreateObjectMetadataDTO dto);

    /* ===================== UPDATE ===================== */

    //@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "objectId", ignore = true),
            @Mapping(target = "bucketName", ignore = true),
            @Mapping(target = "ownerId", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())"),
            @Mapping(target = "tags", ignore = true)
    })
    void updateEntity(UpdateObjectMetadataDTO dto,
                      @MappingTarget ObjectMetadataEntity entity);

    /* ===================== RESPONSE ===================== */

    @Mapping(target = "tags", expression = "java(mapTags(entity))")
    //@Mapping(target = "activeVersion", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")// future feature
    ObjectMetadataResponseDTO toResponse(ObjectMetadataEntity entity);

    /* ===================== TAG MAPPERS ===================== */

    default List<String> mapTags(ObjectMetadataEntity entity) {
        if (entity.getTags() == null) return List.of();
        return entity.getTags()
                .stream()
                .map(ObjectTagEntity::getTag)
                .toList();
    }
}
