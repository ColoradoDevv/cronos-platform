package com.coloradodev.cronos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a cancellation is within the allowed window.
 */
@Documented
@Constraint(validatedBy = CancellationWindowValidator.class)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCancellationWindow {
    String message() default "Cancellation is not allowed at this time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum hours before appointment that cancellation is allowed.
     */
    int minHoursBeforeAppointment() default 24;
}
