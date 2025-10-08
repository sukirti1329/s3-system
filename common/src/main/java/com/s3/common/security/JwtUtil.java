package com.s3.common.security;

import com.s3.common.logging.LoggingUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Utility for generating and validating JWT tokens.
 * Uses a Base64-encoded 256-bit secret shared across all services.
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggingUtil.getLogger(JwtUtil.class);

    private final String secret;
    private final long expirationMillis;
    private Key key;

    public JwtUtil(
            @Value("${auth.jwt.secret}") String secret,
            @Value("${auth.jwt.expiration-ms:86400000}") long expirationMillis) {
        this.secret = secret.trim();
        this.expirationMillis = expirationMillis;
    }

    @PostConstruct
    public void initKey() {
        try {
            // Always treat the secret as Base64-encoded
            byte[] decodedKey = Decoders.BASE64.decode(secret);
            this.key = Keys.hmacShaKeyFor(decodedKey);

            log.info("[JwtUtil] ✅ JWT key initialized (Base64 decoded)");
            log.info("[JwtUtil] Key length={} bytes, algorithm={}", decodedKey.length, key.getAlgorithm());
            log.debug("[JwtUtil] Decoded key bytes={}", Arrays.toString(decodedKey));

        } catch (WeakKeyException e) {
            log.error("❌ JWT key too weak or invalid! Provided secret length: {}", secret.length(), e);
            throw e;
        } catch (Exception e) {
            log.error("❌ Failed to initialize JWT key: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to initialize JWT key", e);
        }
    }

    /** Generate a signed JWT token for a subject and claims. */
    public String generateToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Validate a JWT and return claims if valid. */
    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT validation failed: expired at {}", e.getClaims().getExpiration());
            throw new JwtException("Token expired", e);
        } catch (SignatureException e) {
            log.warn("JWT validation failed: invalid signature");
            throw new JwtException("Invalid signature", e);
        } catch (MalformedJwtException e) {
            log.warn("JWT validation failed: malformed token");
            throw new JwtException("Malformed token", e);
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            throw new JwtException("Invalid token", e);
        }
    }

    public String extractUsername(String token) {
        return validateToken(token).getSubject();
    }

    public String extractClaim(String token, String keyName) {
        Object value = validateToken(token).get(keyName);
        return value != null ? value.toString() : null;
    }
}
