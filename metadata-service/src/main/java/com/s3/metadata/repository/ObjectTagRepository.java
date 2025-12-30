package com.s3.metadata.repository;

import com.s3.metadata.model.ObjectTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ObjectTagRepository
        extends JpaRepository<ObjectTagEntity, UUID> {
}
