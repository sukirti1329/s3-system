package com.s3.metadata.event.consumer;

import com.s3.common.events.model.S3Event;
import com.s3.common.logging.LoggingUtil;
import com.s3.metadata.event.handler.ObjectEventHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MetadataEventConsumer {

    private static final Logger log =
            LoggingUtil.getLogger(MetadataEventConsumer.class);

    private final ObjectEventHandler handler;

    public MetadataEventConsumer(ObjectEventHandler handler) {
        this.handler = handler;
    }

    @KafkaListener(
            topics = "${s3.events.topics.object}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ConsumerRecord<String, S3Event<?>> record) {

        S3Event<?> event = record.value();

        log.info(
                "Consumed event [type={}, eventId={}, key={}]",
                event.getEventType(),
                event.getEventId(),
                record.key()
        );

        handler.handle(event);
    }
}