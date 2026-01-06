package com.s3.bucket.service;

import com.s3.bucket.event.BucketEventService;
import com.s3.bucket.mapper.BucketMapper;
import com.s3.bucket.model.BucketEntity;
import com.s3.bucket.repository.BucketRepository;
import com.s3.common.dto.BucketDTO;
import com.s3.common.exception.InvalidRequestException;
import com.s3.common.exception.ResourceNotFoundException;
import com.s3.common.logging.LoggingUtil;
import org.slf4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BucketService {

    private static final Logger logger = LoggingUtil.getLogger(BucketService.class);

    private final BucketRepository bucketRepository;
    private final BucketMapper bucketMapper;
    private final BucketEventService bucketEventService;

    public BucketService(BucketRepository bucketRepository, BucketMapper bucketMapper, BucketEventService bucketEventService) {
        this.bucketRepository = bucketRepository;
        this.bucketMapper = bucketMapper;
        this.bucketEventService = bucketEventService;
    }

    // ---------------- GET SINGLE ----------------
    @Transactional(readOnly = true)
    public BucketDTO getBucket(String bucketName, String ownerId) {
        logger.info("Fetching bucket '{}' for owner '{}'", bucketName, ownerId);

        BucketEntity entity = bucketRepository
                .findByBucketNameAndOwnerId(bucketName, ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bucket not found"));

        return bucketMapper.toDTO(entity);
    }

    // ---------------- LIST ----------------
    @Cacheable(value = "bucketsByOwner", key = "#ownerId")
    @Transactional(readOnly = true)
    public List<BucketDTO> getListOfBuckets(String ownerId) {
        logger.info("Listing buckets for owner '{}'", ownerId);

        return bucketRepository.findByOwnerId(ownerId)
                .stream()
                .map(bucketMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ---------------- CREATE ----------------
    @CacheEvict(value = "bucketsByOwner", key = "#ownerId")
    public BucketDTO createBucket(String bucketName,
                                  String ownerId,
                                  boolean versioningEnabled) {

        logger.info("Creating bucket '{}' for owner '{}'", bucketName, ownerId);

        if (!StringUtils.hasText(bucketName)) {
            throw new InvalidRequestException("Bucket name cannot be empty");
        }

        bucketRepository.findByBucketNameAndOwnerId(bucketName, ownerId)
                .ifPresent(b -> {
                    throw new InvalidRequestException("Bucket already exists");
                });

        BucketEntity entity = BucketEntity.builder()
                .bucketName(bucketName)
                .ownerId(ownerId)
                .versioningEnabled(versioningEnabled)
                .build();

        BucketEntity saved = bucketRepository.save(entity);

        logger.info("Bucket '{}' created successfully", bucketName);
        return bucketMapper.toDTO(saved);
    }

    // ---------------- UPDATE (NO NAME CHANGE) ----------------
    @CacheEvict(value = "bucketsByOwner", key = "#ownerId")
    public BucketDTO updateBucket(String bucketName,
                                  String ownerId,
                                  boolean versioningEnabled) {

        logger.info("Updating bucket '{}' for owner '{}'", bucketName, ownerId);
        BucketEntity entity = bucketRepository
                .findByBucketNameAndOwnerId(bucketName, ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bucket not found"));
        boolean oldVersioningEnabled = entity.isVersioningEnabled();
        // No-op update protection (optional but clean)
        if (oldVersioningEnabled == versioningEnabled) {
            logger.info(
                    "Bucket '{}' versioning already set to '{}', skipping update",
                    bucketName, versioningEnabled
            );
            return bucketMapper.toDTO(entity);
        }

        entity.setVersioningEnabled(versioningEnabled);

        BucketEntity updated = bucketRepository.save(entity);

        // Emit event ONLY if versioning actually changed
        bucketEventService.publishBucketUpdatedEvent(
                updated.getBucketName(),
                ownerId,
                updated.isVersioningEnabled()
        );

        logger.info("Bucket '{}' updated successfully", bucketName);

        return bucketMapper.toDTO(updated);
    }

    // ---------------- DELETE ----------------
    @CacheEvict(value = "bucketsByOwner", key = "#ownerId")
    public void deleteBucketOfUser(String bucketName, String ownerId) {
        logger.info("Deleting bucket '{}' for owner '{}'", bucketName, ownerId);

        BucketEntity entity = bucketRepository
                .findByBucketNameAndOwnerId(bucketName, ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bucket not found"));

        bucketRepository.delete(entity);
        logger.info("Bucket '{}' deleted successfully", bucketName);
    }
}

