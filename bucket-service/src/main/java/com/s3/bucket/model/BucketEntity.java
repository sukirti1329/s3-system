package com.s3.bucket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(name = "buckets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"bucket_name", "owner_id"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BucketEntity {

        @Id
        @Column(name = "bucket_name", nullable = false, unique = true)
        private String bucketName;

        @Column(name = "owner_id", nullable = false)
        private String ownerId;

        @Column(name = "versioning_enabled", nullable = false)
        private boolean versioningEnabled = false;

        @Column(name = "created_at", updatable = false, insertable = false,
                columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
        private Instant createdAt;
}
