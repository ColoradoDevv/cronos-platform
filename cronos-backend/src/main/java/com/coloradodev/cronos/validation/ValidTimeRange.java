package com.coloradodev.cronos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Class-level annotation to validate that endTime is after startTime.
 * Apply to DTOs that have both startTime and endTime fields.
 */
@Documented
@Constraint(validatedBy = TimeRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTimeRange {
    String message() default "End time must be after start time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    String startField() default "startTime";
    String endField() default "endTime";
}
