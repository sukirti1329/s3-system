package com.s3.bucket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "buckets")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BucketEntity {


        @Id
        @Column(nullable = false, unique = true)
        private String bucketName;

        @Column(nullable = false)
        private String ownerId;

        @Column(name = "versioning_enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
        private boolean versioningEnabled = false;
        @Column(name = "created_at", updatable = false, insertable = false,
                columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
        private Instant createdAt = Instant.now();

        // Getters and Setters
    }
