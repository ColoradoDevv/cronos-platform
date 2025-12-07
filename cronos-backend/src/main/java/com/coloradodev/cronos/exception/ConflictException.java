package com.coloradodev.cronos.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a resource conflict occurs.
 * HTTP 409
 */
public class ConflictException extends CronosException {

    public ConflictException(String message) {
        super(message, "CONFLICT", HttpStatus.CONFLICT);
    }

    public ConflictException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.CONFLICT);
    }
}
