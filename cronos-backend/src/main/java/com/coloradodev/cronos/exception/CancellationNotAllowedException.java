package com.coloradodev.cronos.exception;

/**
 * Exception thrown when a cancellation request violates cancellation policies.
 * HTTP 400
 */
public class CancellationNotAllowedException extends BusinessRuleException {

    public CancellationNotAllowedException(String message) {
        super("CANCELLATION_NOT_ALLOWED", message);
    }

    public static CancellationNotAllowedException alreadyCancelled() {
        return new CancellationNotAllowedException("Booking is already cancelled");
    }

    public static CancellationNotAllowedException alreadyCompleted() {
        return new CancellationNotAllowedException("Cannot cancel a completed appointment");
    }

    public static CancellationNotAllowedException tooLate(int minHours) {
        return new CancellationNotAllowedException(
                String.format("Cancellation requires at least %d hours notice", minHours));
    }

    public static CancellationNotAllowedException appointmentInProgress() {
        return new CancellationNotAllowedException("Cannot cancel an appointment that is in progress");
    }
}
