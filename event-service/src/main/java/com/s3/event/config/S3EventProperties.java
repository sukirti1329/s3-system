package com.s3.event.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "s3.event")
public class S3EventProperties {

    private String serviceName;
    private Map<String, String> topics;
}
