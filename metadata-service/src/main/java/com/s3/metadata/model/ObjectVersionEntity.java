package com.s3.metadata.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "object_versions", uniqueConstraints = @UniqueConstraint(
        columnNames = {"object_id", "version_number"}))
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjectVersionEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String objectId;

    @Column(nullable = false)
    private int versionNumber;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private String ownerId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant createdAt;
}
