package com.s3.common.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "Request payload for user login")
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @Schema(description = "Username of the registered user", example = "alice123")
    private String username;

    @Schema(description = "User's password", example = "Password@123")
    private String password;
}
