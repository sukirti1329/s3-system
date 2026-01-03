package com.s3.event.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;

import org.springframework.boot.ssl.SslBundles;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory(
            KafkaProperties kafkaProperties,
            SslBundles sslBundles
    ) {
        return new DefaultKafkaProducerFactory<>(
                kafkaProperties.getProducer().buildProperties(sslBundles)
        );
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}