package com.s3.bucket.repository;

import com.s3.bucket.model.BucketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BucketRepository extends JpaRepository<BucketEntity, String> {
    List<BucketEntity> findByOwnerId(String ownerId);
    Optional<BucketEntity> findByBucketNameAndOwnerId(String bucketName, String ownerId);

}

