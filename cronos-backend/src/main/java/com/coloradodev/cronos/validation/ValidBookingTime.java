package com.coloradodev.cronos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a booking time is valid:
 * - In the future
 * - Within business hours
 * - Has sufficient lead time
 */
@Documented
@Constraint(validatedBy = BookingTimeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBookingTime {
    String message() default "Booking time is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum hours in advance required for booking.
     */
    int minLeadTimeHours() default 0;

    /**
     * Maximum days in advance allowed for booking.
     */
    int maxAdvanceDays() default 90;
}
