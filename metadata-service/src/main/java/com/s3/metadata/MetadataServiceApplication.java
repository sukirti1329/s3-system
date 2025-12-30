package com.s3.metadata;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@OpenAPIDefinition(info = @Info(title = "Object's Metadata Service API", version = "v1"))
@SpringBootApplication(scanBasePackages = {"com.s3.metadata", "com.s3.common"})
@EnableCaching
public class MetadataServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetadataServiceApplication.class, args);
    }
}
