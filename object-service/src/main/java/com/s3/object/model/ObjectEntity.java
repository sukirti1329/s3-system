package com.s3.object.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String id; // still store UUID internally

    @Column(name = "bucket_name", nullable = false)
    private String bucketName;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    private long size;
    private String checksum;

    @Column(nullable = false)
    private String storagePath;

    private Instant uploadedAt = Instant.now();
}
