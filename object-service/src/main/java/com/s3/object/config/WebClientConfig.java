package com.s3.object.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

//    @Bean
//    public WebClient.Builder webClientBuilder() {
//        return WebClient.builder();
//    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter((request, next) -> {

                    Authentication auth =
                            SecurityContextHolder.getContext().getAuthentication();

                    if (auth != null && auth.getCredentials() instanceof String token) {
                        return next.exchange(
                                ClientRequest.from(request)
                                        .header("Authorization", "Bearer " + token)
                                        .build()
                        );
                    }
                    return next.exchange(request);
                });
    }
}
