package com.s3.bucket.mapper;

import com.s3.bucket.model.BucketEntity;
import com.s3.common.dto.BucketDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = false))
public interface BucketMapper {

    BucketEntity toEntity(BucketDTO dto);

    BucketDTO toDTO(BucketEntity entity);
}