package com.s3.object.event.consumer;


import com.s3.common.events.model.S3Event;
import com.s3.common.events.payload.S3EventPayload;
import com.s3.common.logging.LoggingUtil;
import com.s3.object.event.handler.BucketEventHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BucketEventConsumer {

    private static final Logger log = LoggingUtil.getLogger(BucketEventConsumer.class);

    private final BucketEventHandler handler;

    public BucketEventConsumer(BucketEventHandler handler) {
        this.handler = handler;
    }

    @KafkaListener(topics = "s3.bucket.events", groupId = "object-service")
    public void consume(ConsumerRecord<String, S3Event<? extends S3EventPayload>> record) {
        S3Event<? extends S3EventPayload> event = record.value();
        log.info(
                "Consumed event [type={}, eventId={}, key={}]",
                event.getEventType(),
                event.getEventId(),
                record.key()
        );

        handler.handle(event);
    }
}

