package com.s3.metadata.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "object_versions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjectVersionEntity {

    @Id
    private UUID id;

    private String objectId;
    private int versionNumber;
    private String checksum;
    private String storagePath;
    private boolean isActive;

    @CreationTimestamp
    private Instant createdAt;
}
