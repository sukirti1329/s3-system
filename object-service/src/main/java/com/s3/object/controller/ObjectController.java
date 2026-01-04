package com.s3.object.controller;

import com.s3.common.dto.request.CreateObjectRequestDTO;
import com.s3.common.dto.request.UpdateObjectRequestDTO;
import com.s3.common.dto.response.ObjectResponseDTO;
import com.s3.common.logging.LoggingUtil;
import com.s3.common.response.ApiResponse;
import com.s3.common.security.JwtUserPrincipal;
import com.s3.object.service.ObjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final com.fasterxml.jackson.databind.ObjectMapper mapper =
            new com.fasterxml.jackson.databind.ObjectMapper();
    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    // ----------------------------------------------------------------------
    // CREATE OBJECT
    // ----------------------------------------------------------------------
    @PostMapping(
            value = "/{bucketName}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Upload object to bucket",
            description = "Uploads a new object with metadata into the specified bucket"
    )
    public ResponseEntity<ApiResponse<ObjectResponseDTO>> createObject(
            @Parameter(description = "Bucket name", required = true)
            @PathVariable String bucketName,

            @Parameter(
                    description = "File to upload",
                    required = true,
                    schema = @Schema(type = "string", format = "binary")
            )
            @RequestParam("file")
            MultipartFile file,

            @Parameter(
                    description = "Create object metadata as JSON",
                    required = false,
                    schema = @Schema(implementation = CreateObjectRequestDTO.class)
            )
            @RequestParam(value = "metadata", required = false)
            String metadataJson,

            @AuthenticationPrincipal JwtUserPrincipal user
    ) throws IOException {

        CreateObjectRequestDTO metadata = parseMetadata(metadataJson);

        ObjectResponseDTO response =
                objectService.createObject(
                        bucketName,
                        user.getUserId(),
                        file,
                        metadata
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PatchMapping("/{bucketName}/{objectName}")
    @Operation(summary = "Update object metadata")
    public ResponseEntity<ApiResponse<Void>> updateObject(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestBody UpdateObjectRequestDTO request,
            @AuthenticationPrincipal JwtUserPrincipal user
    ) {

        log.info(
                "User [{}] updating object [{}] in bucket [{}]",
                user.getUserId(),
                objectName,
                bucketName
        );

        objectService.updateObject(
                bucketName,
                objectName,
                user.getUserId(),
                request
        );

        return ResponseEntity.ok(ApiResponse.success());
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
    public ResponseEntity<ApiResponse<List<ObjectResponseDTO>>> listObjects(
            @Parameter(description = "Bucket name", required = true)
            @PathVariable String bucketName,

            @AuthenticationPrincipal JwtUserPrincipal user
    ) {

        log.info(
                "User [{}] listing objects in bucket [{}]",
                user.getUserId(),
                bucketName
        );

        List<ObjectResponseDTO> objects = objectService.listObjects(
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
    public ResponseEntity<ApiResponse<ObjectResponseDTO>> getObject(
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

        ObjectResponseDTO object = objectService.getObject(
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

    private CreateObjectRequestDTO parseMetadata(String json) {
        if (json == null || json.isBlank()) {
            return new CreateObjectRequestDTO(); // defaults apply
        }

        try {
            return mapper.readValue(json, CreateObjectRequestDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid metadata JSON");
        }
    }

}
