package com.s3.object;



import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@OpenAPIDefinition(info = @Info(title = "Object Service API", version = "v1"))
@SpringBootApplication(scanBasePackages = {"com.s3.object", "com.s3.common"})
@EnableCaching
public class ObjectServiceApplication {

//    @PostConstruct
//    public void init() {
//        LoggingUtil.setServiceName("object-service");
//    }

    public static void main(String[] args) {
        SpringApplication.run(ObjectServiceApplication.class, args);
    }
}
