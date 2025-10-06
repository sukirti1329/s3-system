package com.s3.bucket;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Bucket Service API", version = "v1"))
@SpringBootApplication(scanBasePackages = {"com.s3.bucket", "com.s3.common"})
public class BucketServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(BucketServiceApplication.class, args);
    }
}

