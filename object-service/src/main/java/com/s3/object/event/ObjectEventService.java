package com.s3.object.event;

import com.s3.common.dto.request.CreateObjectRequestDTO;
import com.s3.common.dto.request.UpdateObjectRequestDTO;
import com.s3.common.events.enums.S3EventSource;
import com.s3.common.events.enums.S3EventType;
import com.s3.common.events.model.S3Event;
import com.s3.common.events.payload.object.ObjectCreatedPayload;
import com.s3.common.events.payload.object.ObjectDeletedPayload;
import com.s3.common.events.payload.object.ObjectUpdatedPayload;
import com.s3.common.events.service.EventProducer;
import com.s3.object.model.ObjectEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ObjectEventService {

    private static final String OBJECT_TOPIC_KEY = "object";

    private final EventProducer eventProducer;

    public ObjectEventService(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    // ------------------------------------------------------------------
    // CREATE EVENT
    // ------------------------------------------------------------------
    public void publishObjectCreatedEvent(
            ObjectEntity entity,
            String ownerId,
            CreateObjectRequestDTO request
    ) {

        ObjectCreatedPayload createObjectPayload =
                ObjectCreatedPayload.builder()
                        .objectId(entity.getId())
                        .bucketName(entity.getBucketName())
                        .objectKey(entity.getFileName())
                        .description(request.getDescription())
                        .tags(request.getTags())
                        .accessLevel(request.getAccessLevel().toString())
                        .versionEnabled(request.getVersionEnabled())
                        .build();

        S3Event<ObjectCreatedPayload> event =
                S3Event.<ObjectCreatedPayload>builder()
                        .eventId(UUID.randomUUID().toString())
                        .eventType(S3EventType.OBJECT_CREATED)
                        .sourceService(S3EventSource.OBJECT_SERVICE)
                        .ownerId(ownerId)
                        .occurredAt(Instant.now())
                        .payload(createObjectPayload)
                        .build();

        eventProducer.publish(
                OBJECT_TOPIC_KEY,
                entity.getId(), // Kafka key
                event
        );
    }

    // ------------------------------------------------------------------
    // UPDATE EVENT
    // ------------------------------------------------------------------
    public void publishObjectUpdatedEvent(
            ObjectEntity entity,
            String ownerId,
            UpdateObjectRequestDTO request
    ) {

        ObjectUpdatedPayload payload =
                ObjectUpdatedPayload.builder()
                        .objectId(entity.getId())
                        .bucketName(entity.getBucketName())
                        .objectKey(entity.getFileName())
                        .description(request.getDescription())
                        .tags(request.getTags())
                        .accessLevel(request.getAccessLevel())
                        .versionEnabled(request.getVersionEnabled())
                        .build();

        S3Event<ObjectUpdatedPayload> event =
                S3Event.<ObjectUpdatedPayload>builder()
                        .eventId(UUID.randomUUID().toString())
                        .eventType(S3EventType.OBJECT_UPDATED)
                        .sourceService(S3EventSource.OBJECT_SERVICE)
                        .ownerId(ownerId)
                        .occurredAt(Instant.now())
                        .payload(payload)
                        .build();

        eventProducer.publish(
                OBJECT_TOPIC_KEY,
                entity.getId(),
                event
        );
    }

    // ------------------------------------------------------------------
    // DELETE EVENT
    // ------------------------------------------------------------------
    public void publishObjectDeletedEvent(
            ObjectEntity entity,
            String ownerId
    ) {

        ObjectDeletedPayload payload =
                ObjectDeletedPayload.builder()
                        .objectId(entity.getId())
                        .bucketName(entity.getBucketName())
                        .objectKey(entity.getFileName())
                        .build();

        S3Event<ObjectDeletedPayload> event =
                S3Event.<ObjectDeletedPayload>builder()
                        .eventId(UUID.randomUUID().toString())
                        .eventType(S3EventType.OBJECT_DELETED)
                        .sourceService(S3EventSource.OBJECT_SERVICE)
                        .ownerId(ownerId)
                        .occurredAt(Instant.now())
                        .payload(payload)
                        .build();

        eventProducer.publish(
                OBJECT_TOPIC_KEY,
                entity.getId(),   // Kafka key
                event
        );
    }
}