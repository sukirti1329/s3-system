package com.s3.object.service;

import com.s3.common.dto.ObjectDTO;
import com.s3.common.exception.ResourceNotFoundException;
import com.s3.common.logging.LoggingUtil;
import com.s3.object.mapper.ObjectMapper;
import com.s3.object.model.ObjectEntity;
import com.s3.object.repository.ObjectRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.*;
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
        this.objectRepo = objectRepo;
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = mapper;
    }

    public ObjectDTO createObject(String bucketName, String ownerId, MultipartFile file) throws IOException {
        validateBucket(bucketName, ownerId);

        String objectId = UUID.randomUUID().toString();
        Path bucketPath = Paths.get(storageLocation, bucketName);
        Files.createDirectories(bucketPath);

        Path filePath = bucketPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        ObjectEntity entity = ObjectEntity.builder().id(objectId).bucketName(bucketName)
                .fileName(file.getOriginalFilename())
                .size(file.getSize())
                .checksum(calculateChecksum(file.getBytes()))
                .storagePath(filePath.toString())
                .build();

        objectRepo.save(entity);
        log.info("Object {} uploaded to bucket {}", file.getOriginalFilename(), bucketName);
        return objectMapper.toDTO(entity);
    }

    public List<ObjectDTO> listObjects(String bucketName, String ownerId) {
        validateBucket(bucketName, ownerId);

        return objectRepo.findAllByBucketName(bucketName).stream()
                .map(objectMapper::toDTO)
                .toList();
    }

    public ObjectDTO getObject(String bucketName, String objectName, String ownerId) {
        validateBucket(bucketName, ownerId);
        ObjectEntity entity = objectRepo.findByBucketNameAndFileName(bucketName, objectName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Object not found: " + objectName + " in bucket " + bucketName));
        return objectMapper.toDTO(entity);
    }

    public void deleteObject(String bucketName, String objectName, String ownerId) {
        validateBucket(bucketName, ownerId);
        objectRepo.deleteByBucketNameAndFileName(bucketName, objectName);
        log.info("Object {} deleted from bucket {}", objectName, bucketName);
    }

    private void validateBucket(String bucketName, String ownerId) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri(bucketServiceUrl + "/buckets/{bucketName}?ownerId={ownerId}", bucketName, ownerId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Bucket validation failed for bucket={} ownerId={}", bucketName, ownerId);
            throw new ResourceNotFoundException("Bucket not found: " + bucketName);
        }
    }


    private String calculateChecksum(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return "checksum-error";
        }
    }
}
