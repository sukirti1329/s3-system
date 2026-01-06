package com.s3.metadata.event.mapper;

import com.s3.common.dto.request.CreateObjectMetadataDTO;
import com.s3.common.dto.request.UpdateObjectMetadataDTO;
import com.s3.common.events.payload.object.ObjectCreatedPayload;
import com.s3.common.events.payload.object.ObjectUpdatedPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ObjectEventMapper {

    /* ===================== CREATE ===================== */
    @Mapping(target = "versioningEnabled", source = "versionEnabled")
    @Mapping(target = "accessLevel", expression =
            "java(AccessLevel.valueOf(payload.getAccessLevel()))")
    CreateObjectMetadataDTO toCreateDto(ObjectCreatedPayload payload);

    @Mapping(target = "versioningEnabled", source = "versionEnabled")
    UpdateObjectMetadataDTO toUpdateDto(ObjectUpdatedPayload payload);
}