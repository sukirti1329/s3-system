package com.s3.auth.service;


import com.s3.auth.mapper.UserMapper;
import com.s3.auth.model.UserEntity;
import com.s3.auth.repository.UserRepository;
import com.s3.auth.config.JwtUtil;
import com.s3.common.dto.*;
import com.s3.common.exception.ResourceNotFoundException;
import com.s3.common.logging.LoggingUtil;
import org.slf4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggingUtil.getLogger(AuthService.class);

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final UserMapper mapper;

    public AuthService(UserRepository repo, BCryptPasswordEncoder encoder,
                       JwtUtil jwtUtil, UserMapper mapper) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
    }

    public UserDTO register(RegisterRequestDTO req) {
        log.info("Registering user with username={}", req.getUsername());

        if (repo.existsByUsername(req.getUsername())) {
            log.warn("Registration failed: username {} already exists", req.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        if (req.getEmail() != null && repo.existsByEmail(req.getEmail())) {
            log.warn("Registration failed: email {} already exists", req.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        // MapStruct handles base mapping (except password)
        UserEntity entity = mapper.fromRegisterRequest(req);
        entity.setPassword(encoder.encode(req.getPassword()));

        UserEntity saved = repo.save(entity);
        log.info("User registered successfully userId={} username={}", saved.getUserId(), saved.getUsername());

        return mapper.toDTO(saved);
    }

    public AuthResponseDTO login(LoginRequestDTO req) {
        log.info("Attempting login for username={}", req.getUsername());

        UserEntity user = repo.findByUsername(req.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + req.getUsername()));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            log.warn("Login failed for username={} (invalid credentials)", req.getUsername());
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        log.info("User logged in successfully username={} userId={}", user.getUsername(), user.getUserId());
        return new AuthResponseDTO(token, "Bearer");
    }

    public UserDTO getByUserId(String userId) {
        log.info("Fetching user by userId={}", userId);
        UserEntity entity = repo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return mapper.toDTO(entity);
    }
}
