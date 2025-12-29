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

    private final ObjectRepository repository;
    private final ObjectMapper mapper;
    private final WebClient webClient;

    @Value("${storage.location}")
    private String storageLocation;

    @Value("${bucket.service.url:http://localhost:8085}")
    private String bucketServiceUrl;

    public ObjectService(
            ObjectRepository repository,
            ObjectMapper mapper,
            WebClient.Builder webClientBuilder
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.webClient = webClientBuilder.build();
    }

    public ObjectDTO createObject(
            String bucketName,
            String userId,
            MultipartFile file
    ) throws IOException {

        validateBucket(bucketName);
        if (repository.existsByBucketNameAndFileName(bucketName, file.getOriginalFilename())) {
            throw new InvalidRequestException("Object already exists");
        }

        Path bucketPath = Paths.get(storageLocation, bucketName);
        Files.createDirectories(bucketPath);

        Path filePath = bucketPath.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath);

        ObjectEntity entity = ObjectEntity.builder()
                .id(UUID.randomUUID().toString())
                .bucketName(bucketName)
                .fileName(file.getOriginalFilename())
                .size(file.getSize())
                .checksum(calculateChecksum(file.getBytes()))
                .storagePath(filePath.toString())
                .build();

        repository.save(entity);
        return mapper.toDTO(entity);
    }

    public List<ObjectDTO> listObjects(String bucketName, String userId) {
        validateBucket(bucketName);
        return repository.findAllByBucketName(bucketName)
                .stream().map(mapper::toDTO).toList();
    }

    public ObjectDTO getObject(String bucketName, String objectName, String userId) {
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
        try {
            Files.deleteIfExists(Paths.get(entity.getStoragePath()));
        } catch (IOException ignored) {
        }
    }

    private void validateBucket(String bucketName) {
        webClient.get()
                .uri(bucketServiceUrl + "/buckets/{name}", bucketName)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private String calculateChecksum(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(md.digest(data));
        } catch (Exception e) {
            return "checksum-error";
        }
    }
}
