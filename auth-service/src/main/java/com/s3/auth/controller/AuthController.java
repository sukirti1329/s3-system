package com.s3.auth.controller;

import com.s3.auth.service.AuthService;
import com.s3.common.dto.AuthResponseDTO;
import com.s3.common.dto.LoginRequestDTO;
import com.s3.common.dto.RegisterRequestDTO;
import com.s3.common.dto.UserDTO;
import com.s3.common.logging.LoggingUtil;
import com.s3.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller — handles registration, login, and user retrieval.
 * Uses Jwt-based authentication and unified ApiResponse from common module.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and user profile retrieval")
public class AuthController {

    private static final Logger log = LoggingUtil.getLogger(AuthController.class);
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    /**
     * Register a new user.
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns the created user details.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration request",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequestDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "username": "john_doe",
                                              "password": "Password@123",
                                              "email": "john.doe@example.com"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or duplicate user",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    public ResponseEntity<ApiResponse<UserDTO>> register(@RequestBody RegisterRequestDTO request) {
        log.info("Registration request received for username={}", request.getUsername());
        UserDTO createdUser = service.register(request);
        log.info("User registered successfully userId={} username={}", createdUser.getUserId(), createdUser.getUsername());
        //return ResponseEntity.ok(new ApiResponse<>(createdUser));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdUser));

    }

    /**
     * Authenticate an existing user and return a JWT token.
     */
    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Validates user credentials and returns a JWT token if authentication succeeds.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login credentials",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequestDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "username": "john_doe",
                                              "password": "Password@123"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        log.info("Login request received for username={}", request.getUsername());
        AuthResponseDTO token = service.login(request);
        log.info("User logged in successfully username={}", request.getUsername());
        return ResponseEntity.ok(ApiResponse.success(token));
    }

    /**
     * Get user details by userId.
     */
    @GetMapping("/me/{userId}")
    @Operation(
            summary = "Fetch user details by userId",
            description = "Retrieve full details of a specific user using their userId.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User details fetched successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable String userId) {
        log.info("Fetching user profile for userId={}", userId);
        UserDTO user = service.getByUserId(userId);
        return ResponseEntity.ok(
                ApiResponse.success(service.getByUserId(userId))
        );
    }
}
