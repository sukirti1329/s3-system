package com.s3.bucket.service;


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

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BucketService {

    private static final Logger logger = LoggingUtil.getLogger(BucketService.class);
    private final BucketRepository bucketRepository;
    private final BucketMapper bucketMapper;


    public BucketService(BucketRepository bucketRepository, BucketMapper mapper) {
        this.bucketRepository = bucketRepository;
        this.bucketMapper = mapper;
    }


    public BucketDTO getBucket(String bucketName, String ownerId) {
        logger.info("Request received → GET bucket: '{}' for ownerId='{}'", bucketName, ownerId);

        if (!StringUtils.hasText(bucketName) || !StringUtils.hasText(ownerId)) {
            logger.error("Invalid parameters → bucketName='{}', ownerId='{}'", bucketName, ownerId);
            throw new InvalidRequestException("Bucket name and ownerId must not be empty");
        }

        BucketEntity bucket = bucketRepository.findByBucketNameAndOwnerId(bucketName, ownerId)
                .map(entity -> {
                    logger.debug("Bucket found in database → name='{}', ownerId='{}'",
                            entity.getBucketName(), entity.getOwnerId());
                    return entity;
                })
                .orElseThrow(() -> {
                    logger.error("Bucket '{}' not found for owner '{}'", bucketName, ownerId);
                    return new ResourceNotFoundException(
                            "Bucket '" + bucketName + "' for owner '" + ownerId + "' does not exist");
                });

        BucketDTO dto = bucketMapper.toDTO(bucket);
        logger.info("Returning BucketDTO →  name='{}', ownerId='{}'",
                 dto.getBucketName(), dto.getOwnerId());
        return dto;
    }
    @Cacheable(value = "bucketsByOwner", key = "#ownerId")
    public List<BucketDTO> getListOfBuckets(String ownerId) {
        logger.info("Listing buckets for owner: {}", ownerId);
        return bucketRepository.findByOwnerId(ownerId).stream()
                .map(bucketMapper::toDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "bucketsByOwner", key = "#bucketDTO.ownerId")
    public BucketDTO createBucketOfUser(BucketDTO bucketDTO) {
        logger.info("Creating bucket: {} for owner: {}", bucketDTO.getBucketName(), bucketDTO.getOwnerId());
        bucketRepository.findByBucketNameAndOwnerId(bucketDTO.getBucketName(), bucketDTO.getOwnerId())
                .ifPresent(b -> {
                    logger.error("Bucket {} already exists for owner {}", bucketDTO.getBucketName(), bucketDTO.getOwnerId());
                    throw new InvalidRequestException("Bucket already exists");
                });

        BucketEntity bucketEntity = BucketEntity.builder().bucketName(bucketDTO.getBucketName()).versioningEnabled(bucketDTO.isVersioningEnabled()? bucketDTO.isVersioningEnabled():false).ownerId(bucketDTO.getOwnerId()).build();

        BucketEntity savedBucketEntity = bucketRepository.save(bucketEntity);

        BucketEntity refreshedEntity = bucketRepository.findById(savedBucketEntity.getBucketName())
                .orElse(savedBucketEntity);
        logger.info("Bucket {} created successfully", savedBucketEntity.getBucketName());
        return bucketMapper.toDTO(refreshedEntity);
    }

    @CacheEvict(value = "bucketsByOwner", key = "#bucketDTO.ownerId")
    public BucketDTO updateBucket(BucketDTO bucketDTO) {
        logger.info("Updating bucket: {} for ownerId={}", bucketDTO.getBucketName(), bucketDTO.getOwnerId());
        BucketEntity bucket = bucketRepository.findByBucketNameAndOwnerId(bucketDTO.getBucketName(), bucketDTO.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Bucket not found"));

        // Update properties as needed, e.g., versioning flag if added
        // For example, if BucketDTO has versioningEnabled:
        // bucket.setVersioningEnabled(bucketDTO.isVersioningEnabled());
        BucketEntity updatedBucketEntity = BucketEntity.builder().bucketName(bucketDTO.getBucketName()).versioningEnabled(bucketDTO.isVersioningEnabled()? bucketDTO.isVersioningEnabled():false).ownerId(bucketDTO.getOwnerId()).build();

        BucketEntity updated = bucketRepository.save(updatedBucketEntity);
        logger.info("Bucket {} updated successfully", updated.getBucketName());
        return bucketMapper.toDTO(updated);
    }

    @CacheEvict(value = "bucketsByOwner", key = "#ownerId")
    public void deleteBucketOfUser(String bucketName, String ownerId) {
        logger.info("Deleting bucket: {} for owner: {}", bucketName, ownerId);
        BucketEntity bucket = bucketRepository.findByBucketNameAndOwnerId(bucketName, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket not found"));
        bucketRepository.delete(bucket);
        logger.info("Bucket {} deleted successfully", bucketName);
    }





}