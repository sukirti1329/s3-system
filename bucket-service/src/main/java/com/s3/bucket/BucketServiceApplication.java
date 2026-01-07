package com.s3.bucket;

import com.s3.common.config.CommonSecurityConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

@OpenAPIDefinition(info = @Info(title = "Bucket Service API", version = "v1"))
@SpringBootApplication(scanBasePackages = {"com.s3.bucket", "com.s3.common"})
@Import(CommonSecurityConfig.class)
public class BucketServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(BucketServiceApplication.class, args);
    }
}

