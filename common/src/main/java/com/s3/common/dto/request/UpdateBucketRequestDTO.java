package com.s3.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateBucketRequestDTO {
    //Only allow versioningEnabled/Disabled, not name change as it is a primary key and foreign key in backend
    @Schema(description = "Enable or disable versioning", example = "false")
    private boolean versioningEnabled;
}

