package com.s3.common.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Represents a storage bucket in the system")
public class BucketDTO {


    @Schema(description = "Bucket name (must be unique per user)", example = "my-photos")
    private String bucketName;

    @Schema(description = "Owner user ID", example = "user123")
    private String ownerId;

    @Schema(description = "Indicates if versioning is enabled", example = "true")
    private boolean versioningEnabled;

    @Schema(description = "Time of the error", example = "2025-09-25T11:00:00Z")
    private Instant createdAt;
}
