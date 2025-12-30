package com.s3.object.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "objects",
        uniqueConstraints = @UniqueConstraint(columnNames = {"bucket_name", "file_name"})
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjectEntity {

    @Id
    private String id;

    @Column(name = "bucket_name", nullable = false)
    private String bucketName;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private String checksum;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Instant uploadedAt;
}
