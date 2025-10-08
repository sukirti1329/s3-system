package com.s3.auth;


import com.s3.common.config.CommonSecurityConfig;
import com.s3.common.config.JwtProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@OpenAPIDefinition(info = @Info(title = "Authentication Service API", version = "v1"))
@SpringBootApplication(scanBasePackages = {"com.s3.auth", "com.s3.common"})
@EnableConfigurationProperties(JwtProperties.class)
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
