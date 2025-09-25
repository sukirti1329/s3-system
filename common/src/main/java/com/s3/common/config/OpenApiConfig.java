package com.s3.common.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI s3SystemOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("S3 System API")
                        .description("APIs for managing buckets, objects, and authentication in an S3-like system")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Your Team")
                                .email("support@s3-system.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("S3 System Documentation")
                        .url("https://github.com/your-org/s3-system/wiki"));
    }
}
