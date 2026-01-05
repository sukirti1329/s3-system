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

@Mapper(
        componentModel = "spring",
        builder = @org.mapstruct.Builder(disableBuilder = true)  // ✅ Use setters
)
public interface ObjectMetadataMapper {

    /* ===================== CREATE ===================== */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tags", ignore = true)  // ✅ Handle tags manually in service
    @Mapping(target = "activeVersion", ignore = true)
    @Mapping(target = "createdAt", ignore = true)  // ✅ Let @CreationTimestamp handle it
    @Mapping(target = "updatedAt", ignore = true)  // ✅ Let @UpdateTimestamp handle it
    ObjectMetadataEntity toEntity(CreateObjectMetadataDTO dto);

    /* ===================== UPDATE ===================== */

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "objectId", ignore = true)
    @Mapping(target = "bucketName", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)  // ✅ Let @UpdateTimestamp handle it
    @Mapping(target = "tags", ignore = true)  // ✅ Handle tags manually in service
    void updateEntity(UpdateObjectMetadataDTO dto,
                      @MappingTarget ObjectMetadataEntity entity);

    /* ===================== RESPONSE ===================== */

    @Mapping(target = "tags", expression = "java(mapTags(entity))")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ObjectMetadataResponseDTO toResponse(ObjectMetadataEntity entity);

    /* ===================== TAG MAPPERS ===================== */

    default List<String> mapTags(ObjectMetadataEntity entity) {
        if (entity == null || entity.getTags() == null) {
            return List.of();
        }
        return entity.getTags()
                .stream()
                .map(ObjectTagEntity::getTag)
                .toList();
    }
}