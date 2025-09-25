
package com.s3.bucket.controller;

import com.s3.bucket.service.BucketService;
import com.s3.common.dto.BucketDTO;
import com.s3.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Create a new bucket")
    @PostMapping
    public ResponseEntity<ApiResponse<BucketDTO>> createBucket(@RequestBody BucketDTO bucketDTO) {
        BucketDTO created = bucketService.createBucketOfUser(bucketDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(created));
    }

    @Operation(summary = "List buckets for an owner")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BucketDTO>>> listBuckets(@RequestParam String ownerId) {
        List<BucketDTO> buckets = bucketService.getListOfBuckets(ownerId);
        return ResponseEntity.ok(new ApiResponse<>(buckets));
    }
    @Operation(summary = "Get details of a specific bucket")
    @GetMapping("/{bucketName}")
    public ResponseEntity<ApiResponse<BucketDTO>> getBucket(@PathVariable String bucketName,
                                                            @RequestParam String ownerId) {
        BucketDTO bucket = bucketService.getBucket(bucketName, ownerId);
        return ResponseEntity.ok(new ApiResponse<>(bucket));
    }

    @Operation(summary = "Update bucket properties")
    @PutMapping
    public ResponseEntity<ApiResponse<BucketDTO>> updateBucket(@RequestBody BucketDTO bucketDTO) {
        BucketDTO bucket = bucketService.updateBucket(bucketDTO);
        return ResponseEntity.ok(new ApiResponse<>(bucket));
    }

    @Operation(summary = "Delete a bucket")
    @DeleteMapping("/{bucketName}")
    public ResponseEntity<ApiResponse<Void>> deleteBucket(@PathVariable String bucketName, @RequestParam String ownerId) {
        bucketService.deleteBucketOfUser(bucketName, ownerId);
        return ResponseEntity.noContent().build();
    }
}
