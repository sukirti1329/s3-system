package com.s3.bucket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.s3.bucket", "com.s3.common"})
public class BucketServiceApplication {

    //    @PostConstruct
//    public void init() {
//        LoggingUtil.setServiceName("object-service");
//    }
    public static void main(String[] args) {
        SpringApplication.run(BucketServiceApplication.class, args);
    }
}

