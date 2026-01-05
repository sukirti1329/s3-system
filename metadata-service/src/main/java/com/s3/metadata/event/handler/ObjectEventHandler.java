package com.s3.metadata.event.handler;

import com.s3.common.events.model.S3Event;
import com.s3.common.events.payload.object.ObjectCreatedPayload;
import com.s3.common.events.payload.object.ObjectUpdatedPayload;
import com.s3.common.logging.LoggingUtil;
import com.s3.metadata.event.mapper.ObjectEventMapper;
import com.s3.metadata.service.ObjectMetadataService;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ObjectEventHandler {

    private static final Logger log =
            LoggingUtil.getLogger(ObjectEventHandler.class);

    private final ObjectMetadataService metadataService;
    private final ObjectEventMapper mapper;

    public ObjectEventHandler(
            ObjectMetadataService metadataService,
            ObjectEventMapper mapper
    ) {
        this.metadataService = metadataService;
        this.mapper = mapper;
    }

    public void handle(S3Event<?> event) {
        switch (event.getEventType()) {
            case OBJECT_CREATED -> {
                ObjectCreatedPayload payload =
                        (ObjectCreatedPayload) event.getPayload();
                log.info(
                        "Handling OBJECT_CREATED eventId={} objectId={}",
                        event.getEventId(),
                        payload.getObjectId()
                );
                metadataService.create(
                        mapper.toCreateDto(payload),
                        event.getOwnerId()
                );
            }
            case OBJECT_UPDATED -> {
                ObjectUpdatedPayload payload =
                        (ObjectUpdatedPayload) event.getPayload();
                log.info(
                        "Handling OBJECT_UPDATED eventId={} objectId={}",
                        event.getEventId(),
                        payload.getObjectId()
                );
                metadataService.update(
                        payload.getObjectId(),
                        mapper.toUpdateDto(payload)
                );
            }
            default -> log.debug(
                    "Ignoring event type {}",
                    event.getEventType()
            );
        }
    }
}
