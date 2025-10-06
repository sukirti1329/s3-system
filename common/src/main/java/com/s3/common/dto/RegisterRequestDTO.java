package com.s3.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "Request payload for registering a new user")
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @Schema(description = "Desired username", example = "alice123")
    private String username;

    @Schema(description = "User's password (will be encrypted)", example = "Password@123")
    private String password;

    @Schema(description = "User's email address", example = "alice@example.com")
    private String email;
}
