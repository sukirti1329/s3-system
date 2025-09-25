package com.s3.bucket.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "buckets", uniqueConstraints = @UniqueConstraint(columnNames = {"bucketName", "ownerId"}))
@Data
public class BucketEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String bucketName;

        @Column(nullable = false)
        private String ownerId;

        @Column(nullable = false)
        private boolean versioningEnabled;

        @Column(nullable = false)
        private Instant createdAt = Instant.now();

        // Getters and Setters
    }
