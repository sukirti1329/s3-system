package com.s3.object.service;

import com.s3.common.dto.ObjectDTO;
import com.s3.common.exception.InvalidRequestException;
import com.s3.common.exception.ResourceNotFoundException;
import com.s3.common.logging.LoggingUtil;
import com.s3.object.mapper.ObjectMapper;
import com.s3.object.model.ObjectEntity;
import com.s3.object.repository.ObjectRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ObjectService {

    private static final Logger log = LoggingUtil.getLogger(ObjectService.class);

    private final ObjectRepository objectRepository;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;

    @Value("${storage.location}")
    private String storageLocation;

    @Value("${bucket.service.url:http://localhost:8085}")
    private String bucketServiceUrl;

    public ObjectService(
            ObjectRepository objectRepository,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.objectRepository = objectRepository;
        this.objectMapper = objectMapper;
        this.webClientBuilder = webClientBuilder;
    }

    // ----------------------------------------------------------------------
    // CREATE OBJECT
    // ----------------------------------------------------------------------
    public ObjectDTO createObject(
            String bucketName,
            MultipartFile file,
            HttpServletRequest request
    ) throws IOException {

        log.info("Creating object in bucket '{}'", bucketName);
        validateBucket(bucketName, request);

        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("Uploaded file must not be empty");
        }

        String objectId = UUID.randomUUID().toString();
        String fileName = file.getOriginalFilename();

        Path bucketPath = Paths.get(storageLocation, bucketName);
        Files.createDirectories(bucketPath);

        Path filePath = bucketPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        ObjectEntity entity = ObjectEntity.builder()
                .id(objectId)
                .bucketName(bucketName)
                .fileName(fileName)
                .size(file.getSize())
                .checksum(calculateChecksum(file.getBytes()))
                .storagePath(filePath.toString())
                .build();

        objectRepository.save(entity);
        log.info("Object '{}' stored successfully in bucket '{}'", fileName, bucketName);

        return objectMapper.toDTO(entity);
    }

    // ----------------------------------------------------------------------
    // LIST OBJECTS
    // ----------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<ObjectDTO> listObjects(
            String bucketName,
            HttpServletRequest request
    ) {
        log.info("Listing objects in bucket '{}'", bucketName);
        validateBucket(bucketName, request);

        return objectRepository.findAllByBucketName(bucketName)
                .stream()
                .map(objectMapper::toDTO)
                .toList();
    }

    // ----------------------------------------------------------------------
    // GET OBJECT
    // ----------------------------------------------------------------------
    @Transactional(readOnly = true)
    public ObjectDTO getObject(
            String bucketName,
            String objectName,
            HttpServletRequest request
    ) {
        log.info("Fetching object '{}' from bucket '{}'", objectName, bucketName);
        validateBucket(bucketName, request);

        ObjectEntity entity = objectRepository
                .findByBucketNameAndFileName(bucketName, objectName)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Object '" + objectName + "' not found in bucket '" + bucketName + "'"
                        )
                );

        return objectMapper.toDTO(entity);
    }

    // ----------------------------------------------------------------------
    // DELETE OBJECT
    // ----------------------------------------------------------------------
    public void deleteObject(
            String bucketName,
            String objectName,
            HttpServletRequest request
    ) {
        log.info("Deleting object '{}' from bucket '{}'", objectName, bucketName);
        validateBucket(bucketName, request);

        ObjectEntity entity = objectRepository
                .findByBucketNameAndFileName(bucketName, objectName)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Object '" + objectName + "' not found in bucket '" + bucketName + "'"
                        )
                );

        objectRepository.delete(entity);

        Path filePath = Paths.get(entity.getStoragePath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Failed to delete file from filesystem: {}", filePath, e);
        }

        log.info("Object '{}' deleted successfully", objectName);
    }

    // ----------------------------------------------------------------------
    // BUCKET VALIDATION (TOKEN FORWARDING)
    // ----------------------------------------------------------------------
    private void validateBucket(String bucketName, HttpServletRequest request) {

        if (!StringUtils.hasText(bucketName)) {
            throw new InvalidRequestException("Bucket name must not be empty");
        }

        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader)) {
            throw new InvalidRequestException("Missing Authorization header");
        }

        String uri = bucketServiceUrl + "/buckets/" + bucketName;
        log.debug("Validating bucket via bucket-service: {}", uri);

        webClientBuilder.build()
                .get()
                .uri(uri)
                .header("Authorization", authHeader) // 🔥 forward JWT
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        resp -> Mono.error(
                                new ResourceNotFoundException(
                                        "Bucket '" + bucketName + "' not found"
                                )
                        )
                )
                .onStatus(
                        status -> status == HttpStatus.FORBIDDEN || status == HttpStatus.UNAUTHORIZED,
                        resp -> Mono.error(
                                new InvalidRequestException(
                                        "Access denied to bucket '" + bucketName + "'"
                                )
                        )
                )
                .toBodilessEntity()
                .block();

        log.debug("Bucket '{}' validated successfully", bucketName);
    }

    // ----------------------------------------------------------------------
    // CHECKSUM
    // ----------------------------------------------------------------------
    private String calculateChecksum(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(data));
        } catch (Exception e) {
            log.error("Checksum calculation failed", e);
            return "checksum-error";
        }
    }
}
