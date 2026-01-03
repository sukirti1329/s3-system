package com.s3.event.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic objectEventsTopic() {
        return TopicBuilder.name("s3.object.events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic metadataEventsTopic() {
        return TopicBuilder.name("s3.metadata.events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}