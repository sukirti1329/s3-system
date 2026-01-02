package com.s3.common.dto.response;

import com.s3.common.enums.AccessLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Object metadata response")
public class ObjectMetadataResponseDTO {

    @Schema(example = "obj-123")
    private String objectId;

    @Schema(example = "my-bucket")
    private String bucketName;

    @Schema(example = "user-123")
    private String ownerId;

    @Schema(example = "PRIVATE")
    private AccessLevel accessLevel;

    @Schema(description = "Description of object")
    private String description;

    @Schema(description = "Tags associated with object")
    private List<String> tags;

    @Schema(description = "Current active version number")
    private Integer activeVersion;

    private Instant createdAt;
    private Instant updatedAt;
}
