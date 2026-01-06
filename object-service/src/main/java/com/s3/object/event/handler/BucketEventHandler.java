package com.s3.object.event.handler;

import com.s3.common.events.model.S3Event;
import com.s3.common.events.payload.bucket.BucketDeletedPayload;
import com.s3.common.events.payload.bucket.BucketUpdatedPayload;
import com.s3.common.events.payload.object.ObjectCreatedPayload;
import com.s3.common.events.payload.object.ObjectDeletedPayload;
import com.s3.common.events.payload.object.ObjectUpdatedPayload;
import com.s3.common.logging.LoggingUtil;
import com.s3.object.event.mapper.BucketEventMapper;
import com.s3.object.service.ObjectService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BucketEventHandler {

    private static final Logger log =
            LoggingUtil.getLogger(BucketEventHandler.class);

    private final ObjectService objectService;
    private final BucketEventMapper mapper;
    //private final EventIdempotencyService idempotencyService;

    public void handle(S3Event<?> event) {

//        if (idempotencyService.isAlreadyProcessed(event.getEventId())) {
//            log.warn(
//                    "Skipping already processed eventId={}",
//                    event.getEventId()
//            );
//            return;
//        }

        switch (event.getEventType()) {

            case BUCKET_UPDATED -> handleBucketUpdated(
                    (S3Event<BucketUpdatedPayload>) event
            );

            case BUCKET_DELETED -> handleBucketDeleted(
                    (S3Event<BucketDeletedPayload>) event
            );


            default -> log.debug(
                    "Ignoring event type {}",
                    event.getEventType()
            );
        }

//        idempotencyService.markProcessed(
//                event.getEventId(),
//                event.getEventType().name(),
//                event.getSourceService()
//        );
    }

    private void handleBucketUpdated(S3Event<BucketUpdatedPayload> event) {
        BucketUpdatedPayload payload = event.getPayload();

        objectService.updateObjectsByBucket(
                payload.getBucketName(),
                event.getOwnerId(),
                payload.isVersioningEnabled()
        );
    }

    private void handleBucketDeleted(
            S3Event<BucketDeletedPayload> event
    ) {
        BucketDeletedPayload payload = event.getPayload();

        objectService.deleteObjectsByBucket(
                payload.getBucketName(),
                event.getOwnerId()
        );
    }
}
