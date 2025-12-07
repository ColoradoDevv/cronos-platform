package com.coloradodev.cronos.exception;

import java.time.LocalDateTime;

/**
 * Exception thrown when a booking time slot is not available.
 * HTTP 409
 */
public class SlotConflictException extends ConflictException {

    private final LocalDateTime requestedTime;

    public SlotConflictException(String message) {
        super(message, "SLOT_CONFLICT");
        this.requestedTime = null;
    }

    public SlotConflictException(LocalDateTime requestedTime) {
        super(
                String.format("Time slot at %s is not available", requestedTime),
                "SLOT_CONFLICT");
        this.requestedTime = requestedTime;
        this.withDetail("requestedTime", requestedTime.toString());
    }

    public LocalDateTime getRequestedTime() {
        return requestedTime;
    }
}
