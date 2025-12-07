package com.coloradodev.cronos.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Base exception class for all Cronos application exceptions.
 * Provides consistent error handling with HTTP status codes and error codes.
 */
@Getter
public abstract class CronosException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final Map<String, String> details;

    protected CronosException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = new HashMap<>();
    }

    protected CronosException(String message, String errorCode, HttpStatus httpStatus,
            Map<String, String> details) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = details != null ? details : new HashMap<>();
    }

    protected CronosException(String message, String errorCode, HttpStatus httpStatus,
            Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = new HashMap<>();
    }

    /**
     * Add a detail to the exception.
     */
    public CronosException withDetail(String key, String value) {
        this.details.put(key, value);
        return this;
    }
}
