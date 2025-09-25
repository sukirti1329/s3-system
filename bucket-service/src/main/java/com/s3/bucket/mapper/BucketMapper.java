package com.s3.bucket.mapper;

import com.s3.bucket.model.BucketEntity;
import com.s3.common.dto.BucketDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BucketMapper {
    BucketMapper INSTANCE = Mappers.getMapper(BucketMapper.class);

    //@Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    BucketEntity toEntity(BucketDTO dto);

    BucketDTO toDTO(BucketEntity entity);
}
