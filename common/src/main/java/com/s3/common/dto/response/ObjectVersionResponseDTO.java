package com.s3.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response for version details for an object")
public class ObjectVersionResponseDTO {
    private int versionNumber;
    private boolean active;
    private String checksum;
    private String storagePath;
}
