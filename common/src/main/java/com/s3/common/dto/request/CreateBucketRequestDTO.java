package com.s3.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreateBucketRequestDTO {

    @Schema(description = "Bucket name (must be unique per user)", example = "my-photos", required = true)
    private String bucketName;

    @Schema(description = "Enable versioning for this bucket", example = "true")
    private boolean versioningEnabled;
}
