package com.s3.object.service;

import com.s3.common.events.enums.S3EventType;
import com.s3.common.events.model.S3Event;
import com.s3.common.events.service.EventProducer;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ObjectEventService {

    private final EventProducer eventProducer;

    public ObjectEventService(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    public void publishObjectCreatedEvent(
            String objectId,
            String bucketName,
            String ownerId
    ) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("objectId", objectId);
        payload.put("bucketName", bucketName);
        payload.put("ownerId", ownerId);

        S3Event event = S3Event.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(S3EventType.OBJECT_CREATED)
                .sourceService("object-service")
                .occurredAt(Instant.now())
                .payload(payload)
                .build();

        eventProducer.publish(
                "object",
                objectId,   // key
                event
        );
    }

}