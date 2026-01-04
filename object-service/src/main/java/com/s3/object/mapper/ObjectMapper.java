package com.s3.object.mapper;

import com.s3.common.dto.response.ObjectResponseDTO;
import com.s3.object.model.ObjectEntity;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = false))
public interface ObjectMapper {

    ObjectEntity toEntity(ObjectResponseDTO dto);

    ObjectResponseDTO toDTO(ObjectEntity entity);
}
