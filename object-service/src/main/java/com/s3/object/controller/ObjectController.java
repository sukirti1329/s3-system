package com.s3.object.controller;

import com.s3.common.dto.ObjectDTO;
import com.s3.common.logging.LoggingUtil;
import com.s3.common.response.ApiResponse;
import com.s3.common.security.JwtUserPrincipal;
import com.s3.object.service.ObjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/objects")
@Tag(
        name = "Object Management",
        description = "APIs for managing objects inside buckets owned by authenticated users"
)
public class ObjectController {

    private static final Logger log = LoggingUtil.getLogger(ObjectController.class);
    private final ObjectService objectService;

    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    // ----------------------------------------------------------------------
    // CREATE OBJECT
    // ----------------------------------------------------------------------
    @PostMapping(
            value = "/{bucketName}",
            consumes = "multipart/form-data"
    )
    @Operation(
            summary = "Upload object to bucket",
            description = "Uploads a new object into the specified bucket owned by the authenticated user."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Object uploaded successfully"
    )
    public ResponseEntity<ApiResponse<ObjectDTO>> createObject(
            @Parameter(
                    description = "Bucket name",
                    required = true
            )
            @PathVariable String bucketName,
            @Parameter(
                    description = "File to upload",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal JwtUserPrincipal user
    ) throws IOException {
        log.info(
                "User [{}] uploading object [{}] to bucket [{}]",
                user.getUserId(),
                file.getOriginalFilename(),
                bucketName
        );
        ObjectDTO object = objectService.createObject(
                bucketName,
                user.getUserId(),
                file
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(object));
    }


    // ----------------------------------------------------------------------
    // LIST OBJECTS
    // ----------------------------------------------------------------------
    @GetMapping("/{bucketName}")
    @Operation(
            summary = "List objects in bucket",
            description = "Retrieves all objects stored in a bucket owned by the authenticated user.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Objects retrieved successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Bucket not found"
                    )
            }
    )
    public ResponseEntity<ApiResponse<List<ObjectDTO>>> listObjects(
            @Parameter(description = "Bucket name", required = true)
            @PathVariable String bucketName,

            @AuthenticationPrincipal JwtUserPrincipal user
    ) {

        log.info(
                "User [{}] listing objects in bucket [{}]",
                user.getUserId(),
                bucketName
        );

        List<ObjectDTO> objects = objectService.listObjects(
                bucketName,
                user.getUserId()
        );

        log.info(
                "Found [{}] objects in bucket [{}] for user [{}]",
                objects.size(),
                bucketName,
                user.getUserId()
        );

        return ResponseEntity.ok(ApiResponse.success(objects));
    }

    // ----------------------------------------------------------------------
    // GET OBJECT
    // ----------------------------------------------------------------------
    @GetMapping("/{bucketName}/{objectName}")
    @Operation(
            summary = "Get object metadata",
            description = "Fetches metadata of a specific object stored in a bucket owned by the authenticated user.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Object found"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Object or bucket not found"
                    )
            }
    )
    public ResponseEntity<ApiResponse<ObjectDTO>> getObject(
            @Parameter(description = "Bucket name", required = true)
            @PathVariable String bucketName,

            @Parameter(description = "Object name", required = true)
            @PathVariable String objectName,

            @AuthenticationPrincipal JwtUserPrincipal user
    ) {

        log.info(
                "User [{}] fetching object [{}] from bucket [{}]",
                user.getUserId(),
                objectName,
                bucketName
        );

        ObjectDTO object = objectService.getObject(
                bucketName,
                objectName,
                user.getUserId()
        );

        return ResponseEntity.ok(ApiResponse.success(object));
    }

    // ----------------------------------------------------------------------
    // DELETE OBJECT
    // ----------------------------------------------------------------------
    @DeleteMapping("/{bucketName}/{objectName}")
    @Operation(
            summary = "Delete object",
            description = "Deletes an object from a bucket owned by the authenticated user.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Object deleted successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Object or bucket not found"
                    )
            }
    )
    public ResponseEntity<ApiResponse<Void>> deleteObject(
            @Parameter(description = "Bucket name", required = true)
            @PathVariable String bucketName,

            @Parameter(description = "Object name", required = true)
            @PathVariable String objectName,

            @AuthenticationPrincipal JwtUserPrincipal user
    ) {

        log.info(
                "User [{}] deleting object [{}] from bucket [{}]",
                user.getUserId(),
                objectName,
                bucketName
        );

        objectService.deleteObject(
                bucketName,
                objectName,
                user.getUserId()
        );

        log.info(
                "Object [{}] deleted from bucket [{}] by user [{}]",
                objectName,
                bucketName,
                user.getUserId()
        );

        return ResponseEntity.ok(ApiResponse.success());
    }

    // ----------------------------------------------------------------------
    // Download OBJECT
    // ----------------------------------------------------------------------
    @GetMapping("/{bucketName}/{objectName}/download")
    @Operation(
            summary = "Download object",
            description = "Downloads an object from a bucket owned by the authenticated user",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "File downloaded successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Object not found"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized"
                    )
            }
    )
    public ResponseEntity<Resource> downloadObject(
            @PathVariable String bucketName,
            @PathVariable String objectName
    ) {
        return objectService.downloadObject(bucketName, objectName);
    }

}
