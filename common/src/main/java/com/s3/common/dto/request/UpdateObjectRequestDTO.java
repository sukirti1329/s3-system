package com.s3.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update object metadata request")
public class UpdateObjectRequestDTO {

    private String description;
    private List<String> tags;
    private String accessLevel;
}