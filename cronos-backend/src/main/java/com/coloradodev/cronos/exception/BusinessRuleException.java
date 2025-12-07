package com.coloradodev.cronos.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a business rule is violated.
 * HTTP 400
 */
public class BusinessRuleException extends CronosException {

    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION", HttpStatus.BAD_REQUEST);
    }

    public BusinessRuleException(String errorCode, String message) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
    }
}
