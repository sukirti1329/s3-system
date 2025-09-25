package com.s3.bucket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.s3.bucket", "com.s3.common"})
public class BucketServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BucketServiceApplication.class, args);
    }
}

