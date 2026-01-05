package com.s3.common.dto.request;

import com.s3.common.enums.AccessLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Schema(description = "Request to create metadata for an object")
public class CreateObjectMetadataDTO {

    @Schema(description = "Object ID (from object-service)", example = "obj-123", required = true)
    private String objectId;

    @Schema(description = "Bucket name", example = "user-photos", required = true)
    private String bucketName;

    @Schema(description = "Access level of the object", example = "PRIVATE")
    private AccessLevel accessLevel = AccessLevel.PRIVATE;

    @Schema(description = "Optional description of the object")
    private String description;

    @Schema(description = "Tags associated with object")
    private List<String> tags;

    @Schema(description = "Enable or disable versioning")
    private boolean versioningEnabled = true;

}
