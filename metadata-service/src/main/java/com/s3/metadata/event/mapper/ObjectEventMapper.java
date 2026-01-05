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

//    @Mapping(target = "objectId", source = "objectId")
//    @Mapping(target = "bucketName", source = "bucketName")
//    @Mapping(target = "objectKey", source = "objectKey")
//    @Mapping(target = "size", source = "size")
//    @Mapping(target = "checksum", source = "checksum")
//    @Mapping(target = "contentType", source = "contentType")
//    @Mapping(target = "description", source = "description")
//    @Mapping(target = "accessLevel", source = "accessLevel")
//    @Mapping(target = "versionEnabled", source = "versionEnabled")
//    @Mapping(target = "tags", source = "tags")
//    CreateObjectMetadataDTO toCreateDto(ObjectCreatedPayload payload);
//
//    /* ===================== UPDATE ===================== */
//
//    @Mapping(target = "description", source = "description")
//    @Mapping(target = "accessLevel", source = "accessLevel")
//    @Mapping(target = "versionEnabled", source = "versionEnabled")
//    @Mapping(target = "tags", source = "tags")
//    UpdateObjectMetadataDTO toUpdateDto(ObjectUpdatedPayload payload);


    @Mapping(target = "versioningEnabled", source = "versionEnabled")
    @Mapping(target = "accessLevel", expression =
            "java(AccessLevel.valueOf(payload.getAccessLevel()))")
    CreateObjectMetadataDTO toCreateDto(ObjectCreatedPayload payload);

    UpdateObjectMetadataDTO toUpdateDto(ObjectUpdatedPayload payload);
}