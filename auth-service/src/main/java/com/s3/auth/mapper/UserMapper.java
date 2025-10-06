package com.s3.auth.mapper;

import com.s3.auth.model.UserEntity;
import com.s3.common.dto.RegisterRequestDTO;
import com.s3.common.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = false), unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    // Convert Entity → DTO (for responses)
    UserDTO toDTO(UserEntity entity);

    // Convert DTO → Entity (rarely used here, but useful for updates)
    UserEntity toEntity(UserDTO dto);

    // Convert RegisterRequest → Entity (for creation)
    @Mapping(target = "id", ignore = true)
    //@Mapping(target = "userId", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "userId", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "password", ignore = true)  // password is encoded in service
    UserEntity fromRegisterRequest(RegisterRequestDTO request);
}
