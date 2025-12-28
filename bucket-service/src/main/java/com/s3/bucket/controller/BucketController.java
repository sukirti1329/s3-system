package com.s3.bucket.controller;

import com.s3.bucket.service.BucketService;
import com.s3.common.dto.BucketDTO;
import com.s3.common.dto.request.CreateBucketRequestDTO;
import com.s3.common.dto.request.UpdateBucketRequestDTO;
import com.s3.common.logging.LoggingUtil;
import com.s3.common.response.ApiResponse;
import com.s3.common.security.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/buckets")
@Tag(name = "Buckets", description = "Bucket Management APIs")
public class BucketController {

    private final BucketService bucketService;

    public BucketController(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    // ---------------- CREATE ----------------
    @PostMapping
    public ResponseEntity<ApiResponse<BucketDTO>> createBucket(
            @RequestBody CreateBucketRequestDTO request,
            @AuthenticationPrincipal JwtUserPrincipal user) {

        BucketDTO created = bucketService.createBucket(
                request.getBucketName(),
                user.getUserId(),
                request.isVersioningEnabled()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(created));
    }

    // ---------------- LIST ----------------
    @GetMapping
    public ResponseEntity<ApiResponse<List<BucketDTO>>> listBuckets(
            @AuthenticationPrincipal JwtUserPrincipal user) {

        return ResponseEntity.ok(
                new ApiResponse<>(bucketService.getListOfBuckets(user.getUserId()))
        );
    }

    // ---------------- GET ----------------
    @GetMapping("/{bucketName}")
    public ResponseEntity<ApiResponse<BucketDTO>> getBucket(
            @PathVariable String bucketName,
            @AuthenticationPrincipal JwtUserPrincipal user) {

        return ResponseEntity.ok(
                new ApiResponse<>(bucketService.getBucket(bucketName, user.getUserId()))
        );
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/{bucketName}")
    public ResponseEntity<ApiResponse<BucketDTO>> updateBucket(
            @PathVariable String bucketName,
            @RequestBody UpdateBucketRequestDTO request,
            @AuthenticationPrincipal JwtUserPrincipal user) {

        BucketDTO updated = bucketService.updateBucket(
                bucketName,
                user.getUserId(),
                request.isVersioningEnabled()
        );

        return ResponseEntity.ok(new ApiResponse<>(updated));
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/{bucketName}")
    public ResponseEntity<ApiResponse<Void>> deleteBucket(
            @PathVariable String bucketName,
            @AuthenticationPrincipal JwtUserPrincipal user) {

        bucketService.deleteBucketOfUser(bucketName, user.getUserId());
        return ResponseEntity.noContent().build();
    }
}
