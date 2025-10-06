package com.s3.common.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "User details DTO")
public class UserDTO {

    @Schema(description = "Unique user identifier (UUID)", example = "5f8e3e1e-7b3a-4a2b-bc45-fd8b1234abcd")
    private String userId;

    @Schema(description = "User's login username", example = "john_doe")
    private String username;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's role within the system", example = "USER")
    private String role;

    @Schema(description = "Account creation timestamp", example = "2025-10-06T10:23:00Z")
    private Instant createdAt;
}




