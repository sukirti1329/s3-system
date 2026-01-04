package com.s3.event.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisherImpl<T> implements EventPublisher<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    public EventPublisherImpl(KafkaTemplate<String, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String topic, String key, T event) {
        kafkaTemplate.send(topic, key, event);
    }
}