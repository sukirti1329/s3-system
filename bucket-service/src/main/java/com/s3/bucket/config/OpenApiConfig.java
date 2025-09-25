//package com.s3.bucket.config;
//
//import io.swagger.v3.oas.models.ExternalDocumentation;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Contact;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.info.License;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class OpenApiConfig {
//
//    @Bean
//    public OpenAPI bucketServiceOpenAPI() {
//        return new OpenAPI()
//                .info(new Info().title("Bucket Service API")
//                        .description("APIs for managing S3-like buckets")
//                        .version("v1.0.0")
//                        .contact(new Contact()
//                                .name("Your Team")
//                                .email("support@s3-system.com"))
//                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
//                .externalDocs(new ExternalDocumentation()
//                        .description("Project Wiki")
//                        .url("https://github.com/your-org/s3-system/wiki"));
//    }
//}
//
