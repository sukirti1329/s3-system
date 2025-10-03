package com.s3.object.mapper;

import com.s3.common.dto.ObjectDTO;
import com.s3.object.model.ObjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ObjectMapper {
    ObjectMapper INSTANCE = Mappers.getMapper(ObjectMapper.class);

    //@Mapping(source = "id", target = "objectKey")
    ObjectEntity toEntity(ObjectDTO dto);

   // @Mapping(source = "objectKey", target = "id")
    ObjectDTO toDTO(ObjectEntity entity);
}
