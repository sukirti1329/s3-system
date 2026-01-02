package com.s3.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request to create version details for an object")
public class CreateObjectVersionDTO {

    @Schema(description = "Checksum or hash of the object content", example = "5f4dcc3b5aa765d61d8327deb882cf99")
    private String checksum;

    @Schema(description = "Storage path of the object", example = "/my-bucket/images", required = true)
    private String storagePath;
}
