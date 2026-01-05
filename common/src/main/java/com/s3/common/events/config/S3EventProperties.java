package com.s3.common.events.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "s3.events")
public class S3EventProperties {

    private String serviceName;
    private Map<String, String> topics;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<String, String> getTopics() {
        return topics;
    }

    public void setTopics(Map<String, String> topics) {
        this.topics = topics;
    }
}
