package com.s3.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Data transfer object representing an object stored in a bucket")
public class ObjectDTO {


    @Schema(description = "Unique identifier for the object", example = "123e4567-e89b-12d3-a456-426614174000")
    private String objectKey;

    @Schema(description = "Name of the bucket where object is stored", example = "my-bucket")
    private String bucketName;

    @Schema(description = "Name of the file", example = "photo.jpg")
    private String fileName;

    @Schema(description = "Size of the object in bytes", example = "1024")
    private long size;

    @Schema(description = "Checksum or hash of the object content", example = "5f4dcc3b5aa765d61d8327deb882cf99")
    private String checksum;

    @Schema(description = "Timestamp when the object was uploaded", example = "2025-10-02T10:15:30Z")
    private Instant uploadedAt;
}
