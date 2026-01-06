package com.s3.bucket.event;

import com.s3.common.events.config.S3EventProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(S3EventProperties.class)
public class EventPropertiesConfig {
}