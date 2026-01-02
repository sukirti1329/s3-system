package com.s3.common.dto.request;

import com.s3.common.enums.AccessLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema(description = "Request to update metadata for an object")
public class UpdateObjectMetadataDTO {

    @Schema(description = "Access level of the object", example = "PRIVATE")
    private AccessLevel accessLevel = AccessLevel.PRIVATE;

    @Schema(description = "Optional description of the object")
    private String description;

    @Schema(description = "Tags associated with object")
    private List<String> tags;

    private Boolean versioningEnabled;
}
