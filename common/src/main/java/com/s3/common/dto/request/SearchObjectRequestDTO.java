package com.s3.common.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SearchObjectRequestDTO {
    @Schema(description = "Bucket name", example = "bucket1")
    private String bucketName;

    @Schema(description = "Partial file name",example = "homeDoc.pdf")
    private String fileName;

    @Schema(description = "Partial description", example = "Home Documents")
    private String description;

    @Schema(description = "Tags (OR semantics)", example = "[\"images\", \"doc\"]"
    )
    private List<String> tags;
}
