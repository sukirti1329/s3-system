package com.s3.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final String secret = "s3-secret"; // TODO: externalize later

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);
                Claims claims = Jwts.parser()
                        .setSigningKey(secret.getBytes())
                        .parseClaimsJws(token)
                        .getBody();

                request.setAttribute("userId", claims.get("userId"));
                request.setAttribute("role", claims.get("role"));

            } catch (Exception e) {
                System.err.println("JWT parsing failed: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
