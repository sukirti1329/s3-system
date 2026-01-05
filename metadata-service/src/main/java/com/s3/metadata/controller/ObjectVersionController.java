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

import com.s3.common.dto.response.ObjectVersionResponseDTO;
import com.s3.common.logging.LoggingUtil;
import com.s3.common.response.ApiResponse;
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

    private static final Logger log =
            LoggingUtil.getLogger(ObjectVersionController.class);

    private final ObjectVersionService service;

    /* =====================================================
       LIST ALL VERSIONS
       ===================================================== */

    @GetMapping
    @Operation(summary = "List all versions of an object")
    public ResponseEntity<ApiResponse<List<ObjectVersionResponseDTO>>> list(
            @PathVariable String objectId
    ) {
        log.info("Listing versions for objectId={}", objectId);
        return ResponseEntity.ok(
                ApiResponse.success(service.listVersions(objectId))
        );
    }

    /* =====================================================
       GET ACTIVE VERSION
       ===================================================== */

    @GetMapping("/active")
    @Operation(summary = "Get active version of an object")
    public ResponseEntity<ApiResponse<ObjectVersionResponseDTO>> getActive(
            @PathVariable String objectId
    ) {
        log.info("Fetching active version for objectId={}", objectId);
        return ResponseEntity.ok(
                ApiResponse.success(service.getActiveVersion(objectId))
        );
    }

    /* =====================================================
       ROLLBACK TO VERSION
       ===================================================== */

    @PostMapping("/rollback/{versionNumber}")
    @Operation(summary = "Rollback object to a specific version")
    public ResponseEntity<ApiResponse<Void>> rollback(
            @PathVariable String objectId,
            @PathVariable int versionNumber
    ) {
        log.warn(
                "Rollback requested for objectId={} to version={}",
                objectId,
                versionNumber
        );
        service.rollback(objectId, versionNumber);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /* =====================================================
       DELETE ALL VERSIONS (INTERNAL / FUTURE)
       ===================================================== */

    @DeleteMapping
    @Operation(
            summary = "Delete all versions of an object (internal)",
            description = "Intended for internal cleanup or admin use only"
    )
    public ResponseEntity<ApiResponse<Void>> deleteAll(
            @PathVariable String objectId
    ) {
        log.warn("Deleting all versions for objectId={}", objectId);
        service.deleteByObjectId(objectId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}