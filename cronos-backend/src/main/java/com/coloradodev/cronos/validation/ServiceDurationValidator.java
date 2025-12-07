package com.coloradodev.cronos.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for service duration constraints.
 */
public class ServiceDurationValidator implements ConstraintValidator<ValidServiceDuration, Integer> {

    private int min;
    private int max;

    @Override
    public void initialize(ValidServiceDuration constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Integer duration, ConstraintValidatorContext context) {
        if (duration == null) {
            return true; // Let @NotNull handle null cases
        }

        if (duration < min || duration > max) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("Service duration must be between %d and %d minutes", min, max))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
