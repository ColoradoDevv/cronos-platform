package com.coloradodev.cronos.core.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error response for API errors.
 * Provides consistent error format across all endpoints.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * Timestamp when the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code.
     */
    private int status;

    /**
     * HTTP status reason phrase (e.g., "Bad Request", "Not Found").
     */
    private String error;

    /**
     * Machine-readable error code for client-side error handling.
     * Examples: "RESOURCE_NOT_FOUND", "VALIDATION_ERROR", "ACCESS_DENIED"
     */
    private String code;

    /**
     * Human-readable error message.
     */
    private String message;

    /**
     * Request path that caused the error.
     */
    private String path;

    /**
     * Field-level error details for validation errors.
     * Key = field name, Value = error message
     */
    private Map<String, String> details;

    /**
     * Correlation ID for log tracing and debugging.
     */
    private String traceId;

    /**
     * Stack trace or additional debug info (only in debug mode).
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String debugInfo;

    /**
     * Create a simple error response with basic info.
     */
    public static ErrorResponse of(int status, String error, String code, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .code(code)
                .message(message)
                .path(path)
                .build();
    }
}
