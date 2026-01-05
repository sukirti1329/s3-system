package com.s3.metadata.event.handler;

import com.s3.common.events.model.S3Event;
import com.s3.common.events.payload.object.ObjectCreatedPayload;
import com.s3.common.events.payload.object.ObjectDeletedPayload;
import com.s3.common.events.payload.object.ObjectUpdatedPayload;
import com.s3.common.logging.LoggingUtil;
import com.s3.metadata.event.idempotency.EventIdempotencyService;
import com.s3.metadata.event.mapper.ObjectEventMapper;
import com.s3.metadata.service.ObjectMetadataService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObjectEventHandler {
    private static final Logger log =
            LoggingUtil.getLogger(ObjectEventHandler.class);

    private final ObjectMetadataService metadataService;
    private final ObjectEventMapper mapper;
    private final EventIdempotencyService idempotencyService;

    public void handle(S3Event<?> event) {

        if (idempotencyService.isAlreadyProcessed(event.getEventId())) {
            log.warn(
                    "Skipping already processed eventId={}",
                    event.getEventId()
            );
            return;
        }

        switch (event.getEventType()) {

            case OBJECT_CREATED -> {
                metadataService.create(
                        mapper.toCreateDto(
                                (ObjectCreatedPayload) event.getPayload()
                        ),
                        event.getOwnerId()
                );
            }

            case OBJECT_UPDATED -> {
                ObjectUpdatedPayload payload =
                        (ObjectUpdatedPayload) event.getPayload();

                metadataService.update(
                        payload.getObjectId(),
                        mapper.toUpdateDto(payload)
                );
            }

            case OBJECT_DELETED -> {
                ObjectDeletedPayload payload =
                        (ObjectDeletedPayload) event.getPayload();

                metadataService.deleteByObjectId(payload.getObjectId());
            }
            default -> log.debug(
                    "Ignoring event type {}",
                    event.getEventType()
            );
        }

        idempotencyService.markProcessed(
                event.getEventId(),
                event.getEventType().name(),
                event.getSourceService()
        );
    }
}