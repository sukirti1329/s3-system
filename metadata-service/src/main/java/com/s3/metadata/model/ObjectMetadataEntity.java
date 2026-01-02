package com.s3.metadata.model;

import java.time.Instant;

import com.s3.common.enums.AccessLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Entity
@Table(name = "object_metadata")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjectMetadataEntity {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String objectId;

    private String bucketName;
    private String ownerId;

    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel;

    private String description;

    @OneToMany(mappedBy = "metadata", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObjectTagEntity> tags;



    @Column(name = "versioning_enabled", nullable = false)
    private boolean versioningEnabled = true;

    @Column(name = "active_version")
    private Integer activeVersion;


    @Column(name = "created_at", updatable = false, insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant createdAt;

    @Column(name = "updated_at", updatable = false, insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant updatedAt;
}
