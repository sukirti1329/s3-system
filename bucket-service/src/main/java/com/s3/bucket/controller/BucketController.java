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
@Tag(name = "Buckets", description = "Bucket Management APIs for authenticated users")
public class BucketController {

    private static final Logger log = LoggingUtil.getLogger(BucketController.class);
    private final BucketService bucketService;

    public BucketController(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    // ---------------- CREATE ----------------
    @Operation(
            summary = "Create a new bucket",
            description = "Creates a new bucket for the authenticated user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateBucketRequestDTO.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Bucket created successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or duplicate bucket"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<BucketDTO>> createBucket(
            @RequestBody CreateBucketRequestDTO request,
            @AuthenticationPrincipal JwtUserPrincipal user) {

        log.info("User [{}] creating bucket [{}]", user.getUserId(), request.getBucketName());

        BucketDTO created = bucketService.createBucket(
                request.getBucketName(),
                user.getUserId(),
                request.isVersioningEnabled()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(created));

    }

    // ---------------- LIST ----------------
    @Operation(
            summary = "List all buckets for authenticated user",
            description = "Retrieves a list of all buckets owned by the authenticated user.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Buckets retrieved successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<BucketDTO>>> listBuckets(
            @AuthenticationPrincipal JwtUserPrincipal user) {

        log.info("User [{}] listing buckets", user.getUserId());

        return ResponseEntity.ok(
                ApiResponse.success(
                        bucketService.getListOfBuckets(user.getUserId())
                )
        );
    }

    // ---------------- GET ----------------
    @Operation(
            summary = "Get bucket details",
            description = "Fetches details of a specific bucket owned by the authenticated user.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bucket found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Bucket not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/{bucketName}")
    public ResponseEntity<ApiResponse<BucketDTO>> getBucket(
            @PathVariable String bucketName,
            @AuthenticationPrincipal JwtUserPrincipal user) {

        log.info("User [{}] fetching bucket [{}]", user.getUserId(), bucketName);

        return ResponseEntity.ok(
                ApiResponse.success(
                        bucketService.getBucket(bucketName, user.getUserId())
                )
        );

    }

    // ---------------- UPDATE ----------------
    @Operation(
            summary = "Update bucket settings",
            description = "Updates bucket configuration such as versioning. Bucket name cannot be changed.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateBucketRequestDTO.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bucket updated"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Bucket not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PutMapping("/{bucketName}")
    public ResponseEntity<ApiResponse<BucketDTO>> updateBucket(
            @PathVariable String bucketName,
            @RequestBody UpdateBucketRequestDTO request,
            @AuthenticationPrincipal JwtUserPrincipal user) {

        log.info("User [{}] updating bucket [{}]", user.getUserId(), bucketName);

        BucketDTO updated = bucketService.updateBucket(
                bucketName,
                user.getUserId(),
                request.isVersioningEnabled()
        );

        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    // ---------------- DELETE ----------------
    @Operation(
            summary = "Delete a bucket",
            description = "Deletes a bucket owned by the authenticated user.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Bucket deleted"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Bucket not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @DeleteMapping("/{bucketName}")
    public ResponseEntity<ApiResponse<Void>> deleteBucket(
            @PathVariable String bucketName,
            @AuthenticationPrincipal JwtUserPrincipal user) {

        log.info("User [{}] deleting bucket [{}]", user.getUserId(), bucketName);

        bucketService.deleteBucketOfUser(bucketName, user.getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }
}
