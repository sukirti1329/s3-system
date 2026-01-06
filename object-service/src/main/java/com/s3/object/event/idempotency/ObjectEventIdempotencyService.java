package com.s3.object.event.idempotency;

import com.s3.common.events.enums.S3EventSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObjectEventIdempotencyService {

    private final ObjectProcessedEventRepository repository;

    @Transactional(readOnly = true)
    public boolean isAlreadyProcessed(String eventId) {
        return repository.existsById(eventId);
    }

    @Transactional
    public void markProcessed(
            String eventId,
            String eventType,
            S3EventSource sourceService
    ) {
        repository.save(
                new ObjectProcessedEventEntity(
                        eventId,
                        eventType.toString(),
                        sourceService.toString(),
                        null
                )
        );
    }
}

