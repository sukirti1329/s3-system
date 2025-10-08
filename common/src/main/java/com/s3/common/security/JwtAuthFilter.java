package com.s3.common.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String uri = request.getRequestURI();

        // Debug: Print all headers from Swagger
        log.debug("[JwtAuthFilter] URI: {}", uri);
        log.debug("[JwtAuthFilter] Authorization header: '{}'", authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("[JwtAuthFilter] No Bearer token found — continuing without authentication");
            filterChain.doFilter(request, response);
            return;

        }

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.debug("[JwtAuthFilter] Header: {} = {}", headerName, request.getHeader(headerName));
        }


        final String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateToken(token);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(authority));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("[JwtAuthFilter] ✅ Authenticated user '{}', role={}", username, role);
            }

        } catch (Exception e) {
            log.warn("[JwtAuthFilter] ❌ JWT validation failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
