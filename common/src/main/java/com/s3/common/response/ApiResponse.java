package com.s3.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiResponse<T> {

    private T data;
    private ErrorResponse error;
    private boolean success;

    public ApiResponse(T data) {
        this.data = data;
        this.success = true;
    }

    public ApiResponse(ErrorResponse error) {
        this.error = error;
        this.success = false;
    }
}
