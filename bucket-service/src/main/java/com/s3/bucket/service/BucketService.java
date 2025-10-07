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

    public BucketService(BucketRepository bucketRepository, BucketMapper mapper) {
        this.bucketRepository = bucketRepository;
        this.bucketMapper = mapper;
    }

    @Transactional(readOnly = true)
    public BucketDTO getBucket(String bucketName, String ownerId) {
        logger.info("Fetching bucket '{}' for owner '{}'", bucketName, ownerId);

        if (!StringUtils.hasText(bucketName) || !StringUtils.hasText(ownerId)) {
            throw new InvalidRequestException("Bucket name and ownerId must not be empty");
        }

        BucketEntity entity = bucketRepository.findByBucketNameAndOwnerId(bucketName, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Bucket '%s' for owner '%s' not found", bucketName, ownerId)
                ));

        return bucketMapper.toDTO(entity);
    }

    @Cacheable(value = "bucketsByOwner", key = "#ownerId")
    @Transactional(readOnly = true)
    public List<BucketDTO> getListOfBuckets(String ownerId) {
        logger.info("Listing buckets for owner '{}'", ownerId);
        return bucketRepository.findByOwnerId(ownerId).stream()
                .map(bucketMapper::toDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "bucketsByOwner", key = "#bucketDTO.ownerId")
    public BucketDTO createBucketOfUser(BucketDTO bucketDTO) {
        logger.info("Creating new bucket '{}' for owner '{}'",
                bucketDTO.getBucketName(), bucketDTO.getOwnerId());

        bucketRepository.findByBucketNameAndOwnerId(bucketDTO.getBucketName(), bucketDTO.getOwnerId())
                .ifPresent(b -> {
                    throw new InvalidRequestException(
                            String.format("Bucket '%s' already exists for this user", bucketDTO.getBucketName()));
                });

        BucketEntity entity = bucketMapper.toEntity(bucketDTO);
        BucketEntity saved = bucketRepository.save(entity);

        logger.info("Bucket '{}' created successfully for owner '{}'",
                saved.getBucketName(), saved.getOwnerId());
        return bucketMapper.toDTO(saved);
    }

    @CacheEvict(value = "bucketsByOwner", key = "#bucketDTO.ownerId")
    public BucketDTO updateBucket(BucketDTO bucketDTO) {
        logger.info("Updating bucket '{}' for owner '{}'",
                bucketDTO.getBucketName(), bucketDTO.getOwnerId());

        BucketEntity existing = bucketRepository.findByBucketNameAndOwnerId(bucketDTO.getBucketName(), bucketDTO.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Bucket not found"));

        existing.setVersioningEnabled(bucketDTO.isVersioningEnabled());
        BucketEntity updated = bucketRepository.save(existing);

        logger.info("Bucket '{}' updated successfully", updated.getBucketName());
        return bucketMapper.toDTO(updated);
    }

    @CacheEvict(value = "bucketsByOwner", key = "#ownerId")
    public void deleteBucketOfUser(String bucketName, String ownerId) {
        logger.info("Deleting bucket '{}' for owner '{}'", bucketName, ownerId);

        BucketEntity bucket = bucketRepository.findByBucketNameAndOwnerId(bucketName, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket not found"));

        bucketRepository.delete(bucket);
        logger.info("Bucket '{}' deleted successfully for owner '{}'", bucketName, ownerId);
    }
}
