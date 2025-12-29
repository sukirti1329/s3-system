package com.s3.object.controller;

import com.s3.common.dto.ObjectDTO;
import com.s3.common.response.ApiResponse;
import com.s3.common.security.JwtUserPrincipal;
import com.s3.common.logging.LoggingUtil;
import com.s3.object.service.ObjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/objects")
@Tag(name = "Object Management", description = "APIs for managing objects inside buckets")
public class ObjectController {

    private static final Logger log = LoggingUtil.getLogger(ObjectController.class);
    private final ObjectService objectService;

    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    // ---------------- CREATE OBJECT ----------------
    @PostMapping(value = "/{bucketName}", consumes = "multipart/form-data")
    @Operation(
            summary = "Upload object",
            description = "Uploads an object into a bucket owned by the authenticated user"
    )
    public ResponseEntity<ApiResponse<ObjectDTO>> createObject(
            @PathVariable String bucketName,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal JwtUserPrincipal user,
            HttpServletRequest request
    ) throws IOException {

        log.info("User '{}' uploading object to bucket '{}'", user.getUserId(), bucketName);
        ObjectDTO object = objectService.createObject(bucketName, file, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(object));
    }

    // ---------------- LIST OBJECTS ----------------
    @GetMapping("/{bucketName}")
    @Operation(
            summary = "List objects",
            description = "Lists all objects inside a bucket owned by the authenticated user"
    )
    public ResponseEntity<ApiResponse<List<ObjectDTO>>> listObjects(
            @PathVariable String bucketName,
            @AuthenticationPrincipal JwtUserPrincipal user,
            HttpServletRequest request
    ) {
        log.info("User '{}' listing objects in bucket '{}'", user.getUserId(), bucketName);
        return ResponseEntity.ok(
                new ApiResponse<>(objectService.listObjects(bucketName, request))
        );
    }

    // ---------------- GET OBJECT ----------------
    @GetMapping("/{bucketName}/{objectName}")
    @Operation(
            summary = "Get object metadata",
            description = "Fetch object metadata from a bucket owned by the authenticated user"
    )
    public ResponseEntity<ApiResponse<ObjectDTO>> getObject(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @AuthenticationPrincipal JwtUserPrincipal user,
            HttpServletRequest request
    ) {
        log.info("User '{}' fetching object '{}' from bucket '{}'",
                user.getUserId(), objectName, bucketName);

        return ResponseEntity.ok(
                new ApiResponse<>(objectService.getObject(bucketName, objectName, request))
        );
    }

    // ---------------- DELETE OBJECT ----------------
    @DeleteMapping("/{bucketName}/{objectName}")
    @Operation(
            summary = "Delete object",
            description = "Deletes an object from a bucket owned by the authenticated user"
    )
    public ResponseEntity<Void> deleteObject(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @AuthenticationPrincipal JwtUserPrincipal user,
            HttpServletRequest request
    ) {
        log.info("User '{}' deleting object '{}' from bucket '{}'",
                user.getUserId(), objectName, bucketName);

        objectService.deleteObject(bucketName, objectName, request);
        return ResponseEntity.noContent().build();
    }
}
