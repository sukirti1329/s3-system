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

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Utility class for generating and validating JWT tokens.
 * Supports both plain-text and Base64-encoded secrets automatically.
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
            if (isBase64Encoded(secret)) {
                byte[] decodedKey = Decoders.BASE64.decode(secret);
                this.key = Keys.hmacShaKeyFor(decodedKey);
                log.info("JWT key initialized (Base64 decoded) — length={} bytes, algorithm={}",
                        decodedKey.length, key.getAlgorithm());
            } else {
                byte[] rawKey = secret.getBytes(StandardCharsets.UTF_8);
                this.key = Keys.hmacShaKeyFor(rawKey);
                log.info("JWT key initialized (plain text) — length={} bytes, algorithm={}",
                        rawKey.length, key.getAlgorithm());
            }
            System.out.println("[JwtUtil] Initialized with key algorithm: " + key.getAlgorithm());
        } catch (WeakKeyException e) {
            log.error("JWT key is too weak or invalid! Minimum required: 256 bits. Provided secret length: {}",
                    secret.length());
            throw e;
        } catch (Exception e) {
            log.error("Failed to initialize JWT key: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to initialize JWT key", e);
        }
    }

    private boolean isBase64Encoded(String value) {
        return value.matches("^[A-Za-z0-9+/=]+$") && value.length() % 4 == 0;
    }

    /**
     * Generate a signed JWT token for a given subject and claims.
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate a JWT token and return claims if valid.
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("JWT validation failed: Token expired at {}", e.getClaims().getExpiration());
            throw new JwtException("Token expired", e);
        } catch (UnsupportedJwtException e) {
            log.error("JWT validation failed: Unsupported token");
            throw new JwtException("Unsupported token", e);
        } catch (MalformedJwtException e) {
            log.error("JWT validation failed: Malformed token");
            throw new JwtException("Malformed token", e);
        } catch (SignatureException e) {
            log.error("JWT validation failed: Invalid signature");
            throw new JwtException("Invalid signature", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT validation failed: Illegal argument (token is null/empty)");
            throw new JwtException("Invalid token", e);
        }
    }

    /**
     * Extract username (subject) from a valid JWT.
     */
    public String extractUsername(String token) {
        return validateToken(token).getSubject();
    }

    /**
     * Extract a specific claim (e.g., role, userId) from JWT.
     */
    public String extractClaim(String token, String key) {
        Object value = validateToken(token).get(key);
        return value != null ? value.toString() : null;
    }
}
