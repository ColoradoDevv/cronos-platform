package com.coloradodev.cronos.exception;

/**
 * Exception thrown when business hours configuration is invalid.
 * HTTP 400
 */
public class InvalidBusinessHoursException extends BusinessRuleException {

    public InvalidBusinessHoursException(String message) {
        super("INVALID_BUSINESS_HOURS", message);
    }

    public static InvalidBusinessHoursException closeBeforeOpen() {
        return new InvalidBusinessHoursException("Close time must be after open time");
    }

    public static InvalidBusinessHoursException overlapping() {
        return new InvalidBusinessHoursException("Business hours cannot overlap");
    }

    public static InvalidBusinessHoursException invalidDay() {
        return new InvalidBusinessHoursException("Invalid day of week specified");
    }
}
