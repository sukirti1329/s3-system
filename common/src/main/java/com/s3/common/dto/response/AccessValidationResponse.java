package com.s3.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessValidationResponse {
    private boolean allowed;
}
