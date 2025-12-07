package com.coloradodev.cronos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a service duration is within acceptable limits.
 */
@Documented
@Constraint(validatedBy = ServiceDurationValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidServiceDuration {
    String message() default "Service duration must be between {min} and {max} minutes";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum duration in minutes.
     */
    int min() default 5;

    /**
     * Maximum duration in minutes.
     */
    int max() default 480; // 8 hours
}
