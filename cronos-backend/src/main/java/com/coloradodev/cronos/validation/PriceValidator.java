package com.coloradodev.cronos.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

/**
 * Validates that price is positive with appropriate decimal scale.
 */
public class PriceValidator implements ConstraintValidator<ValidPrice, BigDecimal> {

    private int maxScale;

    @Override
    public void initialize(ValidPrice annotation) {
        this.maxScale = annotation.maxScale();
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle required validation
        }
        
        // Must be positive
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        // Check decimal places
        return value.scale() <= maxScale;
    }
}
