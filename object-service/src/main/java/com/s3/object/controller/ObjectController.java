package com.s3.object.controller;

import com.s3.common.dto.ObjectDTO;
import com.s3.common.response.ApiResponse;
import com.s3.object.service.ObjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/objects")
@Tag(name = "Object Management", description = "APIs for managing objects inside buckets")
public class ObjectController {

    private final ObjectService objectService;

    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    @PostMapping("/{bucketName}")
    @Operation(summary = "Create object", description = "Upload a new object into a bucket")
    public ResponseEntity<ApiResponse<ObjectDTO>> createObject(
            @PathVariable String bucketName,
            @RequestParam String ownerId,
            @RequestParam MultipartFile file) throws IOException {

        ObjectDTO object = objectService.createObject(bucketName, ownerId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(object));
    }

    @GetMapping("/{bucketName}")
    @Operation(summary = "List objects", description = "List all objects inside a bucket")
    public ResponseEntity<ApiResponse<List<ObjectDTO>>> listObjects(
            @PathVariable String bucketName,
            @RequestParam String ownerId) {

        List<ObjectDTO> objects = objectService.listObjects(bucketName, ownerId);
        return ResponseEntity.ok(new ApiResponse<>(objects));
    }

    @GetMapping("/{bucketName}/{objectName}")
    @Operation(summary = "Get object", description = "Get details of a specific object")
    public ResponseEntity<ApiResponse<ObjectDTO>> getObject(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam String ownerId) {

        ObjectDTO object = objectService.getObject(bucketName, objectName, ownerId);
        return ResponseEntity.ok(new ApiResponse<>(object));
    }

    @DeleteMapping("/{bucketName}/{objectName}")
    @Operation(summary = "Delete object", description = "Delete an object from a bucket")
    public ResponseEntity<Void> deleteObject(
            @PathVariable String bucketName,
            @PathVariable String objectName,
            @RequestParam String ownerId) {
        objectService.deleteObject(bucketName, objectName, ownerId);
        return ResponseEntity.noContent().build();
    }
}
