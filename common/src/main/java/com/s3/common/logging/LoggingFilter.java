package com.s3.common.logging;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Spring filter to initialize and clear logging context per request.
 */
@Component
public class LoggingFilter  extends OncePerRequestFilter {


    private static final String TRACE_HEADER = "X-Trace-Id";
//    If an upstream service sends X-Trace-Id, we reuse it → trace continuity.Otherwise, generates a new traceId.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // Read traceId if propagated from upstream (API Gateway, another service)
            String traceId = request.getHeader(TRACE_HEADER);

            // Initialize MDC
            LoggingUtil.initContext(traceId);

            filterChain.doFilter(request, response); //Move to dispatcher servlet --> Controller
        } finally {
            LoggingUtil.clear(); // important to avoid MDC leaks between requests
        }
    }
}
