package com.s3.object.event.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectProcessedEventRepository
        extends JpaRepository<ObjectProcessedEventEntity, String> {
}