package com.s3.metadata.controller;

import com.s3.common.dto.request.CreateObjectVersionDTO;
import com.s3.common.dto.response.ObjectVersionResponseDTO;
import com.s3.common.response.ApiResponse;
import com.s3.common.logging.LoggingUtil;
import com.s3.metadata.service.ObjectVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metadata/{objectId}/versions")
@RequiredArgsConstructor
@Tag(name = "Object Versioning")
public class ObjectVersionController {

    private static final Logger log = LoggingUtil.getLogger(ObjectVersionController.class);

    private final ObjectVersionService service;

    /* ================= LIST ================= */

    @GetMapping
    @Operation(summary = "List all versions of an object")
    public ResponseEntity<ApiResponse<List<ObjectVersionResponseDTO>>> list(
            @PathVariable String objectId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(service.listVersions(objectId))
        );
    }

    /* ================= CREATE (INTERNAL) ================= */

    @PostMapping
    @Operation(summary = "Create a new object version (internal)")
    public ResponseEntity<ApiResponse<Void>> create(
            @PathVariable String objectId,
            @RequestBody CreateObjectVersionDTO dto
    ) {
        service.createVersion(objectId, dto);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /* ================= ROLLBACK ================= */

    @PostMapping("/rollback/{versionNumber}")
    @Operation(summary = "Rollback object to a specific version")
    public ResponseEntity<ApiResponse<Void>> rollback(
            @PathVariable String objectId,
            @PathVariable int versionNumber
    ) {
        service.rollback(objectId, versionNumber);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
