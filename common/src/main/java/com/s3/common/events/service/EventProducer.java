package com.s3.common.events.service;
import com.s3.common.events.config.S3EventProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    private static final Logger log = LoggerFactory.getLogger(EventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final S3EventProperties properties;

    public EventProducer(KafkaTemplate<String, Object> kafkaTemplate,
                         S3EventProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    public void publish(String topicKey, String key, Object event) {
        String topic = properties.getTopics().get(topicKey);

        log.info("Publishing event to topic [{}] from [{}]",
                topic, properties.getServiceName());

        kafkaTemplate.send(topic, key, event);
    }
}