package com.s3.object.service;

import com.s3.common.dto.ObjectDTO;
import com.s3.common.exception.InvalidRequestException;
import com.s3.common.exception.ResourceNotFoundException;
import com.s3.common.logging.LoggingUtil;
import com.s3.object.mapper.ObjectMapper;
import com.s3.object.model.ObjectEntity;
import com.s3.object.repository.ObjectRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ObjectService {

    private static final Logger log = LoggingUtil.getLogger(ObjectService.class);

    private final ObjectRepository objectRepo;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${storage.location}")
    private String storageLocation;

    @Value("${bucket.service.url:http://localhost:8085}")
    private String bucketServiceUrl;

    public ObjectService(ObjectRepository objectRepo, WebClient.Builder webClientBuilder, ObjectMapper mapper) {
        this.objectRepo = Objects.requireNonNull(objectRepo, "objectRepo must not be null");
        this.webClientBuilder = Objects.requireNonNull(webClientBuilder, "webClientBuilder must not be null");
        this.objectMapper = Objects.requireNonNull(mapper, "objectMapper must not be null");
    }

    public ObjectDTO createObject(String bucketName, String ownerId, MultipartFile file) throws IOException {
        log.debug("createObject called with bucketName='{}', ownerId='{}', fileName='{}'",
                bucketName, ownerId, file != null ? file.getOriginalFilename() : "null");

        // Validate inputs
        validateParameters(bucketName, ownerId);
        if (file == null || file.isEmpty()) {
            log.error("File is null or empty for bucket='{}'", bucketName);
            throw new InvalidRequestException("Uploaded file must not be null or empty");
        }

        validateBucket(bucketName, ownerId);

        String objectId = UUID.randomUUID().toString();
        Path bucketPath = Paths.get(storageLocation, bucketName);

        try {
            Files.createDirectories(bucketPath);
            log.debug("Ensured bucket directory exists at path='{}'", bucketPath);
        } catch (IOException e) {
            log.error("Failed to create bucket directory at path='{}'", bucketPath, e);
            throw e;
        }

        String filename = file.getOriginalFilename();
        Path filePath = bucketPath.resolve(filename);

        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored file '{}' at '{}'", filename, filePath);
        } catch (IOException e) {
            log.error("Failed to store file '{}' in bucket '{}'", filename, bucketName, e);
            throw e;
        }

        String checksum = calculateChecksum(file.getBytes());
        ObjectEntity entity = ObjectEntity.builder()
                .id(objectId)
                .bucketName(bucketName)
                .fileName(filename)
                .size(file.getSize())
                .checksum(checksum)
                .storagePath(filePath.toString())
                .build();

        objectRepo.save(entity);
        log.info("Object '{}' (ID={}) metadata saved to database", filename, objectId);

        return objectMapper.toDTO(entity);
    }

    public List<ObjectDTO> listObjects(String bucketName, String ownerId) {
        log.debug("listObjects called for bucket='{}', ownerId='{}'", bucketName, ownerId);
        validateParameters(bucketName, ownerId);
        validateBucket(bucketName, ownerId);

        List<ObjectDTO> dtos = objectRepo.findAllByBucketName(bucketName).stream()
                .map(objectMapper::toDTO)
                .toList();

        log.info("Found {} objects in bucket '{}'", dtos.size(), bucketName);
        return dtos;
    }

    public ObjectDTO getObject(String bucketName, String objectName, String ownerId) {
        log.debug("getObject called for bucket='{}', objectName='{}', ownerId='{}'",
                bucketName, objectName, ownerId);
        validateParameters(bucketName, ownerId);
        if (!StringUtils.hasText(objectName)) {
            log.error("Invalid objectName='{}' for bucket='{}'", objectName, bucketName);
            throw new InvalidRequestException("Object name must not be empty");
        }

        validateBucket(bucketName, ownerId);

        ObjectEntity entity = objectRepo.findByBucketNameAndFileName(bucketName, objectName)
                .orElseThrow(() -> {
                    log.error("Object '{}' not found in bucket '{}'", objectName, bucketName);
                    return new ResourceNotFoundException(
                            "Object not found: " + objectName + " in bucket " + bucketName);
                });

        log.info("Retrieved object '{}' metadata for bucket '{}'", objectName, bucketName);
        return objectMapper.toDTO(entity);
    }

    @Transactional
    public boolean deleteObject(String bucketName, String objectName, String ownerId) {
        log.debug("deleteObject called for bucket='{}', objectName='{}', ownerId='{}'",
                bucketName, objectName, ownerId);

        // Validate inputs
        validateParameters(bucketName, ownerId);
        if (!StringUtils.hasText(objectName)) {
            log.error("Invalid objectName='{}' for deletion in bucket='{}'", objectName, bucketName);
            throw new InvalidRequestException("Object name must not be empty");
        }

        // Ensure bucket exists (throws ResourceNotFoundException if not)
        validateBucket(bucketName, ownerId);

        // Verify object exists
        ObjectEntity entity = objectRepo.findByBucketNameAndFileName(bucketName, objectName)
                .orElse(null);
        if (entity == null) {
            log.warn("Object '{}' not found in bucket '{}'", objectName, bucketName);
            return false;
        }

        // Delete metadata
        objectRepo.deleteByBucketNameAndFileName(bucketName, objectName);
        log.info("Deleted metadata for object '{}' (ID={})", objectName, entity.getId());

        // Delete file
        Path filePath = Paths.get(storageLocation, bucketName, objectName);
        try {
            if (Files.deleteIfExists(filePath)) {
                log.info("Deleted file from filesystem: {}", filePath);
            } else {
                log.warn("File did not exist on filesystem: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete file '{}' from filesystem", filePath, e);
        }

        log.info("Completed deletion of object '{}' from bucket '{}'", objectName, bucketName);
        return true;
    }

    // ----------------------------------------------------------------------------------------
    // Internal helpers
    // ----------------------------------------------------------------------------------------

    private void validateParameters(String bucketName, String ownerId) {
        if (!StringUtils.hasText(bucketName) || !StringUtils.hasText(ownerId)) {
            log.error("Invalid parameters: bucketName='{}', ownerId='{}'", bucketName, ownerId);
            throw new InvalidRequestException("Bucket name and ownerId must not be empty");
        }
    }

    private void validateBucket(String bucketName, String ownerId) {
        if (!StringUtils.hasText(bucketName) || !StringUtils.hasText(ownerId)) {
            log.error("validateBucket called with invalid parameters: bucket='{}', ownerId='{}'", bucketName, ownerId);
            throw new InvalidRequestException("Bucket name and ownerId must not be empty");
        }

        String uri = String.format("%s/buckets/%s?ownerId=%s", bucketServiceUrl, bucketName, ownerId);
        log.debug("Calling bucket service → GET {}", uri);

        Mono<ResponseEntity<Void>> responseMono = webClientBuilder.build()
                .get()
                .uri(uri)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), resp -> {
                    HttpStatusCode code = resp.statusCode();
                    if (code == HttpStatus.NOT_FOUND) {
                        log.error("Bucket '{}' not found for owner '{}': 404 Not Found", bucketName, ownerId);
                        return Mono.error(new ResourceNotFoundException(
                                "Bucket '" + bucketName + "' for owner '" + ownerId + "' does not exist"));
                    } else if (code == HttpStatus.UNAUTHORIZED || code == HttpStatus.FORBIDDEN) {
                        log.error("Access denied validating bucket='{}', ownerId='{}': status={}", bucketName, ownerId, code.value());
                        return Mono.error(new ResourceNotFoundException(
                                "Access denied for bucket '" + bucketName + "'"));
                    } else {
                        log.error("Unexpected 4xx status {} validating bucket='{}'", code.value(), bucketName);
                        return Mono.error(new ResourceNotFoundException(
                                "Bucket validation failed with status " + code.value()));
                    }
                })
                .onStatus(resp -> resp.is5xxServerError(), resp -> {
                    HttpStatusCode code = resp.statusCode();
                    log.error("Server error validating bucket='{}': status={}", bucketName, code.value());
                    return Mono.error(new ResourceNotFoundException(
                            "Bucket service error with status " + code.value()));
                })
                .toBodilessEntity()
                .doOnNext(resp -> log.debug("Bucket service response for bucket='{}', ownerId='{}': status={}, headers={}",
                        bucketName, ownerId, resp.getStatusCode(), resp.getHeaders()))
                .doOnError(e -> log.error("Error calling bucket service for bucket='{}', ownerId='{}'", bucketName, ownerId, e));

        ResponseEntity<Void> response = responseMono.block();
        if (response == null) {
            log.error("Null response received from bucket service for bucket='{}'", bucketName);
            throw new ResourceNotFoundException(
                    "Bucket '" + bucketName + "' for owner '" + ownerId + "' does not exist");
        }

        log.info("Bucket '{}' validated successfully for owner '{}'", bucketName, ownerId);
    }

    private String calculateChecksum(byte[] data) {
        if (data == null) {
            log.error("Data for checksum calculation is null");
            return "checksum-error";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);
            String result = Base64.getEncoder().encodeToString(hash);
            log.debug("Calculated SHA-256 checksum");
            return result;
        } catch (Exception e) {
            log.error("Checksum calculation failed", e);
            return "checksum-error";
        }
    }
}
