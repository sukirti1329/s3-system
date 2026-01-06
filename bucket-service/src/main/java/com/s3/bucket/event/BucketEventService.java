package com.s3.bucket.event;

import com.s3.common.events.enums.S3EventSource;
import com.s3.common.events.enums.S3EventType;
import com.s3.common.events.model.S3Event;
import com.s3.common.events.payload.bucket.BucketDeletedPayload;
import com.s3.common.events.payload.bucket.BucketUpdatedPayload;
import com.s3.common.events.service.EventProducer;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class BucketEventService {

    private static final String BUCKET_TOPIC_KEY = "bucket";

    private final EventProducer eventProducer;

    public BucketEventService(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    /* =======================
       Bucket Updated Event
       ======================= */

    public void publishBucketUpdatedEvent(
            String bucketName,
            String ownerId,
            boolean versioningEnabled
    ) {

        BucketUpdatedPayload payload = BucketUpdatedPayload.builder()
                .bucketName(bucketName)
                .versioningEnabled(versioningEnabled)
                .build();

        S3Event<BucketUpdatedPayload> event = S3Event.<BucketUpdatedPayload>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(S3EventType.BUCKET_UPDATED)
                .sourceService(S3EventSource.BUCKET_SERVICE)
                .ownerId(ownerId)
                .occurredAt(Instant.now())
                .payload(payload)
                .build();

        eventProducer.publish(
                BUCKET_TOPIC_KEY,
                bucketName,
                event
        );
    }

    /* =======================
       Bucket Deleted Event
       ======================= */

    public void publishBucketDeletedEvent(
            String bucketName,
            String ownerId
    ) {

        BucketDeletedPayload payload = BucketDeletedPayload.builder()
                .bucketName(bucketName)
                .build();

        S3Event<BucketDeletedPayload> event = S3Event.<BucketDeletedPayload>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(S3EventType.BUCKET_DELETED)
                .sourceService(S3EventSource.BUCKET_SERVICE)
                .ownerId(ownerId)
                .occurredAt(Instant.now())
                .payload(payload)
                .build();

        eventProducer.publish(
                BUCKET_TOPIC_KEY,
                bucketName,
                event
        );
    }
}
