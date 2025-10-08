package com.s3.common.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Arrays;

@Configuration
public class JwtConfig {

    @Value("${auth.jwt.secret}")
    private String secret;

    @Bean
    public Key jwtSigningKey() {
        byte[] decodedKey = Decoders.BASE64.decode(secret.trim());
        System.out.println("[JwtConfig] JWT secret: '" + secret + "'");
        System.out.println("[JwtConfig] decodedKey=" + Arrays.toString(decodedKey));
        return Keys.hmacShaKeyFor(decodedKey);
    }
}
