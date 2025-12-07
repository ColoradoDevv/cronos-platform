package com.coloradodev.cronos.core.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Request logging filter for HTTP request/response logging.
 * Adds correlation ID, logs request info, and measures response time.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";
    private static final String TENANT_ID_MDC_KEY = "tenantId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // Get or generate correlation ID
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString().substring(0, 8);
        }

        // Add to MDC for log correlation
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);

        // Get tenant ID from header if present
        String tenantId = request.getHeader("X-Tenant-ID");
        if (tenantId != null) {
            MDC.put(TENANT_ID_MDC_KEY, tenantId);
        }

        // Add correlation ID to response
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        // Log request
        String requestLog = String.format("[%s] --> %s %s",
                correlationId,
                request.getMethod(),
                getRequestPath(request));
        log.info(requestLog);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Log response with duration
            long duration = System.currentTimeMillis() - startTime;
            String responseLog = String.format("[%s] <-- %s %s - %d (%dms)",
                    correlationId,
                    request.getMethod(),
                    getRequestPath(request),
                    response.getStatus(),
                    duration);

            if (response.getStatus() >= 400) {
                log.warn(responseLog);
            } else if (duration > 1000) {
                // Log slow requests
                log.warn("{} [SLOW]", responseLog);
            } else {
                log.info(responseLog);
            }

            // Clear MDC
            MDC.remove(CORRELATION_ID_MDC_KEY);
            MDC.remove(TENANT_ID_MDC_KEY);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip logging for static resources and health checks
        return path.startsWith("/actuator")
                || path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs")
                || path.endsWith(".js")
                || path.endsWith(".css")
                || path.endsWith(".ico");
    }

    private String getRequestPath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString != null) {
            return request.getRequestURI() + "?" + queryString;
        }
        return request.getRequestURI();
    }
}
