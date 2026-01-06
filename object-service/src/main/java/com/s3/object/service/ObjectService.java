package com.s3.object.service;

import com.s3.common.dto.BucketDTO;
import com.s3.common.dto.request.CreateObjectRequestDTO;
import com.s3.common.dto.request.UpdateObjectRequestDTO;
import com.s3.common.dto.response.ObjectResponseDTO;
import com.s3.common.enums.AccessLevel;
import com.s3.common.exception.InvalidRequestException;
import com.s3.common.exception.ResourceNotFoundException;
import com.s3.common.logging.LoggingUtil;
import com.s3.common.response.ApiResponse;
import com.s3.object.event.ObjectEventService;
import com.s3.object.mapper.ObjectMapper;
import com.s3.object.model.ObjectEntity;
import com.s3.object.repository.ObjectRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

@Service
@Transactional
public class ObjectService {

    private static final Logger log = LoggingUtil.getLogger(ObjectService.class);

    private final ObjectRepository repository;
    private final ObjectMapper mapper;
    private final WebClient webClient;
    private final ObjectEventService objectEventService;

    @Value("${storage.location}")
    private String storageLocation;

    @Value("${bucket.service.url:http://localhost:8085}")
    private String bucketServiceUrl;

    public ObjectService(
            ObjectRepository repository,
            ObjectMapper mapper,
            WebClient.Builder webClientBuilder,
            ObjectEventService objectEventService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.webClient = webClientBuilder.build();
        this.objectEventService = objectEventService;
    }

    public ObjectResponseDTO createObject(
            String bucketName,
            String userId,
            MultipartFile file,
            CreateObjectRequestDTO request
    ) throws IOException {

        BucketDTO bucketDTO = validateAndGetBucket(bucketName);
        boolean versioningEnabled = bucketDTO.isVersioningEnabled();

        // Always override request (never trust client)
        request.setVersionEnabled(versioningEnabled);

        validateFileDetails(file);

        String fileName = file.getOriginalFilename();
        // 3. DB-level object existence check
        if (repository.existsByBucketNameAndFileName(bucketName, fileName)) {
            throw new InvalidRequestException("Object already exists");
        }

        // 4. Prepare filesystem paths
        Path bucketPath = Paths.get(storageLocation, bucketName);
        Files.createDirectories(bucketPath);

        Path filePath = bucketPath.resolve(fileName);
        if (Files.exists(filePath)) {
            throw new InvalidRequestException("File already exists in storage");
        }

        // 5. Persist object metadata
        ObjectEntity entity = ObjectEntity.builder()
                .id(UUID.randomUUID().toString())
                .bucketName(bucketName)
                .fileName(fileName)
                .size(file.getSize())
                .checksum(calculateChecksum(file.getBytes()))
                .storagePath(filePath.toString())
                .contentType(setContentType(file.getContentType()))
                .build();

        repository.save(entity);

        // 6. Publish event (bucket-driven versioning)
        objectEventService.publishObjectCreatedEvent(entity, userId, request);

        // 7. Write file LAST (side effect)
        Files.copy(file.getInputStream(), filePath);

        return mapper.toDTO(entity);
    }

    private static void validateFileDetails(MultipartFile file) {
        String fileName = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename())
        );

        if (!StringUtils.hasText(fileName)) {
            throw new InvalidRequestException("File name is required");
        }

        if (file.isEmpty()) {
            throw new InvalidRequestException("File is empty");
        }

        if (fileName.contains("..")) {
            throw new InvalidRequestException("Invalid file name");
        }
    }

    public void updateObject(
            String bucketName,
            String fileName,
            String userId,
            UpdateObjectRequestDTO request
    ) {

        BucketDTO bucketDTO = validateAndGetBucket(bucketName);
        ObjectEntity entity = repository
                .findByBucketNameAndFileName(bucketName, fileName)
                .orElseThrow(() -> new ResourceNotFoundException("Object/File not found"));

        //request.setVersionEnabled(bucketDTO.isVersioningEnabled());
        objectEventService.publishObjectUpdatedEvent(entity, userId, request, bucketDTO.isVersioningEnabled());
    }
    public List<ObjectResponseDTO> listObjects(String bucketName, String userId) {
        validateBucket(bucketName);
        return repository.findAllByBucketName(bucketName)
                .stream().map(mapper::toDTO).toList();
    }

    public ObjectResponseDTO getObject(String bucketName, String objectName, String userId) {
        validateBucket(bucketName);
        return repository.findByBucketNameAndFileName(bucketName, objectName)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found"));
    }

    public void deleteObject(String bucketName, String objectName, String userId) {
        validateBucket(bucketName);

        ObjectEntity entity = repository
                .findByBucketNameAndFileName(bucketName, objectName)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found"));

        repository.delete(entity);
        // Delete file
        try {
            Files.deleteIfExists(Paths.get(entity.getStoragePath()));
        } catch (IOException e) {
            log.warn("Failed to delete file from storage: {}", entity.getStoragePath(), e);
        }

        // Publish delete event LAST
        objectEventService.publishObjectDeletedEvent(entity, userId);
    }


    public ResponseEntity<Resource> downloadObject(String bucketName, String objectName) {
        log.info("Downloading object '{}' from bucket '{}'", objectName, bucketName);
        validateBucket(bucketName);
        ObjectEntity entity = repository
                .findByBucketNameAndFileName(bucketName, objectName)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Object not found: " + objectName
                        )
                );

        Path filePath = Paths.get(entity.getStoragePath());
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("File missing on storage");
        }
        Resource resource = new FileSystemResource(filePath);
        String contentType = setContentType(entity.getContentType());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + entity.getFileName() + "\""
                )
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(entity.getSize())
                .body(resource);
    }

    private static String setContentType(String entity) {
        String contentType = entity;
        if (!StringUtils.hasText(contentType)) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }


    private void validateBucket(String bucketName) {
        webClient.get()
                .uri(bucketServiceUrl + "/buckets/{name}", bucketName)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private BucketDTO validateAndGetBucket(String bucketName) {

        ApiResponse<BucketDTO> response = webClient.get()
                .uri(bucketServiceUrl + "/buckets/{name}", bucketName)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        r -> Mono.error(new ResourceNotFoundException("Bucket not found: " + bucketName))
                )
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<BucketDTO>>() {})
                .block();

        if (response == null || response.getData() == null) {
            throw new ResourceNotFoundException("Bucket not found: " + bucketName);
        }

        return response.getData();
    }

    private String calculateChecksum(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(md.digest(data));
        } catch (Exception e) {
            return "checksum-error";
        }
    }

    private CreateObjectRequestDTO normalizeCreateRequest(
            CreateObjectRequestDTO createObjectRequestDTO
    ) {
        if (createObjectRequestDTO == null) {
            createObjectRequestDTO = new CreateObjectRequestDTO();
        }

        if (createObjectRequestDTO.getVersionEnabled() == null) {
            createObjectRequestDTO.setVersionEnabled(true);
        }
        if (!StringUtils.hasText(createObjectRequestDTO.getAccessLevel().toString())) {
            createObjectRequestDTO.setAccessLevel(AccessLevel.PRIVATE);
        }

        return createObjectRequestDTO;
    }
}
