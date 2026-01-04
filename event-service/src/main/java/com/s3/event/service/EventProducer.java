//package com.s3.event.service;
//
//import com.s3.event.config.S3EventProperties;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EventProducer {
//
//    private static final Logger log = LoggerFactory.getLogger(EventProducer.class);
//
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//    private final S3EventProperties properties;
//
//    public EventProducer(KafkaTemplate<String, Object> kafkaTemplate,
//                         S3EventProperties properties) {
//        this.kafkaTemplate = kafkaTemplate;
//        this.properties = properties;
//    }
//
//    public <T> void send(String topicKey, String key, T event) {
//
//        String topic = properties.getTopics().get(topicKey);
//
//        if (topic == null) {
//            throw new IllegalArgumentException(
//                    "No Kafka topic configured for key: " + topicKey
//            );
//        }
//
//        log.info(
//                "Publishing event to topic [{}] from service [{}]",
//                topic,
//                properties.getServiceName()
//        );
//
//        kafkaTemplate.send(topic, key, event)
//                .whenComplete((result, ex) -> {
//                    if (ex != null) {
//                        log.error("Failed to publish event to topic [{}]", topic, ex);
//                    } else {
//                        log.debug(
//                                "Event published to topic [{}], partition [{}], offset [{}]",
//                                topic,
//                                result.getRecordMetadata().partition(),
//                                result.getRecordMetadata().offset()
//                        );
//                    }
//                });
//    }
//}
