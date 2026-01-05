package com.s3.metadata.model;

import java.time.Instant;

import com.s3.common.enums.AccessLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "object_metadata")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ObjectMetadataEntity {

    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String objectId;

    @Column(name = "bucket_name")
    private String bucketName;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel;

    @Column(name = "description")
    private String description;

    @OneToMany(
            mappedBy = "metadata",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<ObjectTagEntity> tags = new ArrayList<>();

    @Column(name = "versioning_enabled", nullable = false)
    private boolean versioningEnabled = true;

    @Column(name = "active_version")
    private Integer activeVersion;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant updatedAt;

    // ✅ ADD HELPER METHODS
    public void addTag(ObjectTagEntity tag) {
        tags.add(tag);
        tag.setMetadata(this);
    }

    public void removeTag(ObjectTagEntity tag) {
        tags.remove(tag);
        tag.setMetadata(null);
    }

    public void clearTags() {
        // Create a copy to avoid ConcurrentModificationException
        new ArrayList<>(tags).forEach(this::removeTag);
    }
}
