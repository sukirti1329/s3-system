
package com.s3.object.repository;

import com.s3.object.model.ObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObjectRepository extends JpaRepository<ObjectEntity, String> {
    List<ObjectEntity> findAllByBucketName(String bucketName);
    Optional<ObjectEntity> findByBucketNameAndFileName(String bucketName, String fileName);
    void deleteByBucketNameAndFileName(String bucketName, String fileName);
}

