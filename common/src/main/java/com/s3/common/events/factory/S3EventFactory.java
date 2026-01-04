package com.s3.common.events.factory;

import com.s3.common.events.enums.S3EventSource;
import com.s3.common.events.enums.S3EventType;
import com.s3.common.events.model.S3Event;
import com.s3.common.events.payload.S3EventPayload;

import java.time.Instant;
import java.util.UUID;

public final class S3EventFactory {

    private S3EventFactory() {
    }

    public static <T extends S3EventPayload> S3Event<T> createEvent(
            S3EventType eventType,
            S3EventSource sourceService,
            String ownerId,
            T payload
    ) {
        return S3Event.<T>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .sourceService(sourceService)
                .ownerId(ownerId)
                .occurredAt(Instant.now())
                .payload(payload)
                .build();
    }
}