package com.coloradodev.cronos.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Validator for cancellation window constraints.
 * Can validate both LocalDateTime directly or objects with getStartTime()
 * method.
 */
public class CancellationWindowValidator implements ConstraintValidator<ValidCancellationWindow, Object> {

    private int minHoursBeforeAppointment;

    @Override
    public void initialize(ValidCancellationWindow constraintAnnotation) {
        this.minHoursBeforeAppointment = constraintAnnotation.minHoursBeforeAppointment();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDateTime appointmentTime = null;

        // Handle LocalDateTime directly
        if (value instanceof LocalDateTime) {
            appointmentTime = (LocalDateTime) value;
        }
        // Handle objects with getStartTime() method via reflection
        else {
            try {
                var method = value.getClass().getMethod("getStartTime");
                Object result = method.invoke(value);
                if (result instanceof LocalDateTime) {
                    appointmentTime = (LocalDateTime) result;
                }
            } catch (Exception e) {
                // If we can't get the start time, skip validation
                return true;
            }
        }

        if (appointmentTime == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        long hoursUntilAppointment = ChronoUnit.HOURS.between(now, appointmentTime);

        if (hoursUntilAppointment < minHoursBeforeAppointment) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("Cancellation requires at least %d hours notice. Current notice: %d hours",
                            minHoursBeforeAppointment, Math.max(0, hoursUntilAppointment)))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
