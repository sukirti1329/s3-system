package com.s3.common.response;

import java.time.Instant;

public class ApiResponse<T> {

    private boolean success;
    private T data;
    private ErrorResponse error;
    private Instant timestamp = Instant.now();

    private ApiResponse() {
    }

    // ---------- SUCCESS ----------
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.success = true;
        resp.data = data;
        return resp;
    }

    public static ApiResponse<Void> success() {
        ApiResponse<Void> resp = new ApiResponse<>();
        resp.success = true;
        return resp;
    }

    // ---------- ERROR ----------
    public static ApiResponse<Void> error(ErrorResponse error) {
        ApiResponse<Void> resp = new ApiResponse<>();
        resp.success = false;
        resp.error = error;
        return resp;
    }

    // ---------- GETTERS ----------
    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public ErrorResponse getError() { return error; }
    public Instant getTimestamp() { return timestamp; }
}
