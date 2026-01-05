package com.s3.common.dto.request;

import com.s3.common.enums.AccessLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create object request metadata")
public class CreateObjectRequestDTO {

    @Schema(description = "Object description")
    private String description;

    @Builder.Default
    @Schema(description = "Tags for the object")
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Access level of the object", defaultValue = "PRIVATE")
    private AccessLevel accessLevel = AccessLevel.PRIVATE;

    @Builder.Default
    @Schema(description = "Enable versioning", defaultValue = "true")
    private Boolean versionEnabled = true;
}
