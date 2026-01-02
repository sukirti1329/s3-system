package com.s3.metadata.controller;

import com.s3.common.dto.request.CreateObjectMetadataDTO;
import com.s3.common.logging.LoggingUtil;
import com.s3.common.response.ApiResponse;
import com.s3.common.security.JwtUserPrincipal;
import com.s3.metadata.service.ObjectMetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/metadata")
@Tag(name = "Object's Metadata Management", description = "APIs for managing object's metadata")


public class ObjectMetadataController {

    private static final Logger log = LoggingUtil.getLogger(ObjectMetadataController.class);
    private final ObjectMetadataService service;

    public ObjectMetadataController(ObjectMetadataService service) {
        this.service = service;
    }

    // ---------------- CREATE ----------------
    @PostMapping
    @Operation(
            summary = "Create metadata",
            description = "Creates metadata for an object"
    )
    public ResponseEntity<ApiResponse<CreateObjectMetadataDTO>> create(
            @RequestBody CreateObjectMetadataDTO dto,
            @AuthenticationPrincipal JwtUserPrincipal user
    ) {
        log.info("User [{}] creating metadata for objectId={}", user.getUserId(), dto.getObjectId());
        return ResponseEntity.ok(ApiResponse.success(service.create(dto)));
    }

    // ---------------- GET ----------------
    @GetMapping("/{objectId}")
    @Operation(
            summary = "Get metadata",
            description = "Fetch metadata for a given object"
    )
    public ResponseEntity<ApiResponse<CreateObjectMetadataDTO>> get(
            @PathVariable String objectId,
            @AuthenticationPrincipal JwtUserPrincipal user
    ) {
        log.info("User [{}] fetching metadata for objectId={}", user.getUserId(), objectId);
        return ResponseEntity.ok(ApiResponse.success(service.getByObjectId(objectId)));
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/{objectId}")
    @Operation(
            summary = "Update metadata",
            description = "Update tags, description, or access type"
    )
    public ResponseEntity<ApiResponse<CreateObjectMetadataDTO>> update(
            @PathVariable String objectId,
            @RequestBody CreateObjectMetadataDTO dto,
            @AuthenticationPrincipal JwtUserPrincipal user
    ) {
        log.info("User [{}] updating metadata for objectId={}", user.getUserId(), objectId);
        return ResponseEntity.ok(ApiResponse.success(service.update(objectId, dto)));
    }

    // ---------------- SEARCH ----------------
    @GetMapping("/search")
    @Operation(
            summary = "Search metadata",
            description = "Search objects by metadata tags"
    )
    public ResponseEntity<ApiResponse<List<CreateObjectMetadataDTO>>> search(
            @RequestParam String tag,
            @AuthenticationPrincipal JwtUserPrincipal user
    ) {
        log.info("User [{}] searching metadata by tag={}", user.getUserId(), tag);
        return ResponseEntity.ok(ApiResponse.success(service.searchByTag(tag)));
    }
}
