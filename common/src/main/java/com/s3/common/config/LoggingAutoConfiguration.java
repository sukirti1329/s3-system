package com.s3.common.config;

import com.s3.common.logging.LoggingUtil;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@AutoConfiguration
@ConditionalOnProperty(name = "spring.application.name")
public class LoggingAutoConfiguration {

    @Value("${spring.application.name:unknown-service}")
    private String serviceName;

    @PostConstruct
    public void initializeLogging() {
        LoggingUtil.setServiceName(serviceName);
    }
}
