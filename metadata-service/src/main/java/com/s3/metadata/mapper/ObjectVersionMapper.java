package com.s3.metadata.mapper;

import com.s3.common.dto.response.ObjectVersionResponseDTO;
import com.s3.metadata.model.ObjectVersionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ObjectVersionMapper {
   // @Mapping(target = "active", source = "isActive")
    ObjectVersionResponseDTO toResponse(ObjectVersionEntity entity);

    List<ObjectVersionResponseDTO> toResponseList(List<ObjectVersionEntity> entities);

}
