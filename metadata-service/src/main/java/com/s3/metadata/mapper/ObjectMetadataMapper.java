package com.s3.metadata.mapper;

import com.s3.common.dto.request.CreateObjectMetadataDTO;
import com.s3.metadata.model.ObjectMetadataEntity;
import com.s3.metadata.model.ObjectTagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = false))
public interface ObjectMetadataMapper {
    @Mapping(target = "tags", expression = "java(mapTagsToStrings(entity.getTags()))")
    CreateObjectMetadataDTO toDto(ObjectMetadataEntity entity);

    // ---------- DTO → ENTITY ----------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tags", ignore = true) // handled manually
    ObjectMetadataEntity toEntity(CreateObjectMetadataDTO dto);

    // ---------- HELPERS ----------
    default List<String> mapTagsToStrings(List<ObjectTagEntity> tags) {
        if (tags == null) return List.of();
        return tags.stream()
                .map(ObjectTagEntity::getTag)
                .toList();
    }

    default List<ObjectTagEntity> mapStringsToTags(
            List<String> tags,
            ObjectMetadataEntity metadata
    ) {
        if (tags == null) return List.of();
        return tags.stream()
                .map(t -> ObjectTagEntity.builder()
                        .tag(t)
                        .metadata(metadata)
                        .build())
                .toList();
    }
}
