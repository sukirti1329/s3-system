package com.s3.bucket.controller;

import com.s3.bucket.service.BucketService;
import com.s3.common.dto.BucketDTO;
import com.s3.common.exception.UnauthorizedAccessException;
import com.s3.common.logging.LoggingUtil;
import com.s3.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // --------------------------
    // CREATE BUCKET
    // --------------------------
    @Operation(
            summary = "Create a new bucket for the authenticated user",
            description = "Creates a new bucket owned by the currently authenticated user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Bucket creation payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BucketDTO.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Bucket created successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or duplicate bucket"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)")
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<BucketDTO>> createBucket(
            @RequestBody BucketDTO bucketDTO,
            HttpServletRequest request) {

        String ownerId = (String) request.getAttribute("userId");
        if (ownerId == null) {
            throw new UnauthorizedAccessException("Missing or invalid JWT token");
        }

        bucketDTO.setOwnerId(ownerId);
        log.info("User {} requested bucket creation: {}", ownerId, bucketDTO.getBucketName());

        BucketDTO created = bucketService.createBucketOfUser(bucketDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(created));
    }

    // --------------------------
    // LIST BUCKETS
    // --------------------------
    @Operation(
            summary = "List all buckets for authenticated user",
            description = "Retrieves a list of all buckets owned by the authenticated user.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Buckets retrieved successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)")
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<BucketDTO>>> listBuckets(HttpServletRequest request) {
        String ownerId = (String) request.getAttribute("userId");
        if (ownerId == null) {
            throw new UnauthorizedAccessException("Missing or invalid JWT token");
        }

        log.info("User {} fetching list of buckets", ownerId);
        List<BucketDTO> buckets = bucketService.getListOfBuckets(ownerId);
        return ResponseEntity.ok(new ApiResponse<>(buckets));
    }

    // --------------------------
    // GET SINGLE BUCKET
    // --------------------------
    @Operation(
            summary = "Get details of a specific bucket",
            description = "Fetches detailed information of a bucket by its name, belonging to the authenticated user.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bucket found successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Bucket not found for this user"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)")
            }
    )
    @GetMapping("/{bucketName}")
    public ResponseEntity<ApiResponse<BucketDTO>> getBucket(
            @PathVariable String bucketName,
            HttpServletRequest request) {

        String ownerId = (String) request.getAttribute("userId");
        if (ownerId == null) {
            throw new UnauthorizedAccessException("Missing or invalid JWT token");
        }

        log.info("User {} requested bucket details for {}", ownerId, bucketName);
        BucketDTO bucket = bucketService.getBucket(bucketName, ownerId);
        return ResponseEntity.ok(new ApiResponse<>(bucket));
    }

    // --------------------------
    // UPDATE BUCKET
    // --------------------------
    @Operation(
            summary = "Update bucket properties",
            description = "Allows the authenticated user to update bucket configurations (like versioning).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated bucket properties",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BucketDTO.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bucket updated successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Bucket not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)")
            }
    )
    @PutMapping
    public ResponseEntity<ApiResponse<BucketDTO>> updateBucket(
            @RequestBody BucketDTO bucketDTO,
            HttpServletRequest request) {

        String ownerId = (String) request.getAttribute("userId");
        if (ownerId == null) {
            throw new UnauthorizedAccessException("Missing or invalid JWT token");
        }

        bucketDTO.setOwnerId(ownerId);
        log.info("User {} updating bucket {}", ownerId, bucketDTO.getBucketName());

        BucketDTO updated = bucketService.updateBucket(bucketDTO);
        return ResponseEntity.ok(new ApiResponse<>(updated));
    }

    // --------------------------
    // DELETE BUCKET
    // --------------------------
    @Operation(
            summary = "Delete a bucket",
            description = "Deletes a specific bucket owned by the authenticated user.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Bucket deleted successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Bucket not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)")
            }
    )
    @DeleteMapping("/{bucketName}")
    public ResponseEntity<ApiResponse<Void>> deleteBucket(
            @PathVariable String bucketName,
            HttpServletRequest request) {

        String ownerId = (String) request.getAttribute("userId");
        if (ownerId == null) {
            throw new UnauthorizedAccessException("Missing or invalid JWT token");
        }

        log.info("User {} deleting bucket {}", ownerId, bucketName);
        bucketService.deleteBucketOfUser(bucketName, ownerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
