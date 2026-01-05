//package com.s3.metadata.event;
//
//
//import com.s3.common.events.model.S3Event;
//import com.s3.common.events.enums.S3EventType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ObjectEventConsumer {
//
//    private static final Logger log =
//            LoggerFactory.getLogger(ObjectEventConsumer.class);
//
//    @KafkaListener(
//            topics = "${s3.event.topics.object}",
//            groupId = "${spring.kafka.consumer.group-id}",
//            containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void onObjectEvent(S3Event event) {
//
//        log.info("Received S3 event type={} payload={}",
//                event.getEventType(), event.getPayload());
//
//        // FUTURE:
//        if (event.getEventType() == S3EventType.OBJECT_CREATED) {
//            // create metadata
//        } else if (event.getEventType() == S3EventType.OBJECT_UPDATED) {
//            // update metadata / version
//        }
//    }
//}
