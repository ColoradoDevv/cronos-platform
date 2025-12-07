package com.coloradodev.cronos.exception;

/**
 * Exception thrown when a booking request violates booking policies.
 * HTTP 400
 */
public class BookingNotAllowedException extends BusinessRuleException {

    public BookingNotAllowedException(String message) {
        super("BOOKING_NOT_ALLOWED", message);
    }

    public static BookingNotAllowedException outsideBusinessHours() {
        return new BookingNotAllowedException("Booking time is outside business hours");
    }

    public static BookingNotAllowedException inThePast() {
        return new BookingNotAllowedException("Cannot book appointments in the past");
    }

    public static BookingNotAllowedException tooFarInAdvance(int maxDays) {
        return new BookingNotAllowedException(
                String.format("Cannot book more than %d days in advance", maxDays));
    }

    public static BookingNotAllowedException insufficientLeadTime(int minHours) {
        return new BookingNotAllowedException(
                String.format("Booking requires at least %d hours advance notice", minHours));
    }
}
