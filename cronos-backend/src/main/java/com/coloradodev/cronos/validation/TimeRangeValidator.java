package com.coloradodev.cronos.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

/**
 * Validates that end time is after start time.
 * Supports LocalDateTime and LocalTime fields.
 */
public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, Object> {

    private String startField;
    private String endField;

    @Override
    public void initialize(ValidTimeRange annotation) {
        this.startField = annotation.startField();
        this.endField = annotation.endField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        BeanWrapper wrapper = new BeanWrapperImpl(value);
        Object startValue = wrapper.getPropertyValue(startField);
        Object endValue = wrapper.getPropertyValue(endField);

        if (startValue == null || endValue == null) {
            return true; // Let @NotNull handle required validation
        }

        if (startValue instanceof LocalDateTime start && endValue instanceof LocalDateTime end) {
            return end.isAfter(start);
        }

        if (startValue instanceof LocalTime start && endValue instanceof LocalTime end) {
            return end.isAfter(start);
        }

        return true;
    }
}
