package com.coloradodev.cronos.core.exception;

import com.coloradodev.cronos.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Global exception handler for the Cronos API.
 * Maps exceptions to appropriate HTTP responses with consistent error format.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.debug:false}")
    private boolean debugMode;

    // ==================== CronosException Hierarchy ====================

    /**
     * Handle all CronosException subtypes.
     */
    @ExceptionHandler(CronosException.class)
    public ResponseEntity<ErrorResponse> handleCronosException(
            CronosException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        HttpStatus status = ex.getHttpStatus();

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .details(ex.getDetails().isEmpty() ? null : ex.getDetails())
                .traceId(traceId)
                .debugInfo(debugMode ? getStackTrace(ex) : null)
                .build();

        logException(ex, traceId, status, request);

        return ResponseEntity.status(status).body(error);
    }

    // ==================== Validation Errors ====================

    /**
     * Handle validation errors from @Valid annotations.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ex.getBindingResult().getGlobalErrors()
                .forEach(error -> fieldErrors.put(error.getObjectName(), error.getDefaultMessage()));

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .code("VALIDATION_ERROR")
                .message("Invalid input data. Please check the details.")
                .path(request.getRequestURI())
                .details(fieldErrors)
                .traceId(traceId)
                .build();

        log.warn("[{}] Validation error at {}: {}", traceId, request.getRequestURI(), fieldErrors);

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle type mismatch errors (e.g., invalid UUID format).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        String paramName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .code("INVALID_PARAMETER")
                .message(String.format("Invalid value for parameter '%s'. Expected type: %s",
                        paramName, requiredType))
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        log.warn("[{}] Type mismatch at {}: {} - expected {}", traceId, request.getRequestURI(),
                paramName, requiredType);

        return ResponseEntity.badRequest().body(error);
    }

    // ==================== Security Exceptions ====================

    /**
     * Handle Spring Security access denied exceptions.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        String username = getCurrentUsername();

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .code("ACCESS_DENIED")
                .message("You don't have permission to access this resource")
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        log.warn("[{}] Access denied at {} - User: {}", traceId, request.getRequestURI(), username);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Handle authentication exceptions.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .code("AUTHENTICATION_FAILED")
                .message("Authentication required or invalid credentials")
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        log.warn("[{}] Authentication failed at {}: {}", traceId, request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // ==================== Generic Exceptions ====================

    /**
     * Handle IllegalArgumentException.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .code("INVALID_ARGUMENT")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        log.warn("[{}] Invalid argument at {}: {}", traceId, request.getRequestURI(), ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle IllegalStateException.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .code("INVALID_STATE")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        log.warn("[{}] Invalid state at {}: {}", traceId, request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Catch-all handler for unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .code("INTERNAL_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .path(request.getRequestURI())
                .traceId(traceId)
                .debugInfo(debugMode ? getStackTrace(ex) : null)
                .build();

        log.error("[{}] Internal error at {}: {}", traceId, request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ==================== Helper Methods ====================

    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private String getCurrentUsername() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null ? auth.getName() : "anonymous";
        } catch (Exception e) {
            return "unknown";
        }
    }

    private void logException(CronosException ex, String traceId, HttpStatus status, HttpServletRequest request) {
        String message = String.format("[%s] %s at %s: %s",
                traceId, ex.getErrorCode(), request.getRequestURI(), ex.getMessage());

        if (status.is5xxServerError()) {
            log.error(message, ex);
        } else if (status == HttpStatus.FORBIDDEN || status == HttpStatus.UNAUTHORIZED) {
            log.warn("{} - User: {}", message, getCurrentUsername());
        } else {
            log.warn(message);
        }
    }
}
