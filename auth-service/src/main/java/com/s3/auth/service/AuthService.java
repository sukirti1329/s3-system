package com.s3.auth.service;

import com.s3.auth.model.UserEntity;
import com.s3.auth.repository.UserRepository;
import com.s3.auth.mapper.UserMapper;
import com.s3.common.dto.AuthResponseDTO;
import com.s3.common.dto.LoginRequestDTO;
import com.s3.common.dto.RegisterRequestDTO;
import com.s3.common.dto.UserDTO;
import com.s3.common.exception.InvalidRequestException;
import com.s3.common.exception.ResourceNotFoundException;
import com.s3.common.logging.LoggingUtil;
import com.s3.common.security.JwtUtil;
import org.slf4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    private static final Logger log = LoggingUtil.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, UserMapper userMapper, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    public UserDTO register(RegisterRequestDTO request) {
        log.info("Attempting to register user '{}'", request.getUsername());

        if (request.getUsername() == null || request.getPassword() == null || request.getEmail() == null) {
            throw new InvalidRequestException("Username, password, and email are required");
        }

        userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new InvalidRequestException("Username already exists");
        });

        UserEntity entity = new UserEntity();
        entity.setUserId(UUID.randomUUID().toString());
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setRole("USER");

        userRepository.save(entity);
        return userMapper.toDTO(entity);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Login attempt for '{}'", request.getUsername());

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidRequestException("Invalid credentials");
        }

        Map<String, Object> claims = Map.of(
                "userId", user.getUserId(),
                "role", user.getRole()
        );

        String token = jwtUtil.generateToken(user.getUsername(), claims); // 1 day

        return new AuthResponseDTO(token, "Bearer");
    }

    public UserDTO getByUserId(String userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDTO(user);
    }
}
