package com.s3.object.mapper;

import com.s3.common.dto.ObjectDTO;
import com.s3.object.model.ObjectEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

//@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = false))
@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = false))
//@Mapper(componentModel = "spring")
public interface ObjectMapper {

    ObjectEntity toEntity(ObjectDTO dto);

    ObjectDTO toDTO(ObjectEntity entity);
}
