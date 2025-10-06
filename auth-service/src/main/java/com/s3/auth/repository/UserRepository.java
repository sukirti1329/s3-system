package com.s3.auth.repository;

import com.s3.auth.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByUserId(String userId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
