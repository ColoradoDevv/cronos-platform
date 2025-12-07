package com.coloradodev.cronos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a BigDecimal price is positive and has valid scale.
 */
@Documented
@Constraint(validatedBy = PriceValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPrice {
    String message() default "Price must be positive with at most 2 decimal places";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    int maxScale() default 2;
}
