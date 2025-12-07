package com.coloradodev.cronos.validation;

import com.coloradodev.cronos.core.tenant.TenantContext;
import com.coloradodev.cronos.dto.booking.BookingRequestDTO;
import com.coloradodev.cronos.service.BusinessHoursService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Validator for booking time constraints.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingTimeValidator implements ConstraintValidator<ValidBookingTime, BookingRequestDTO> {

    private final BusinessHoursService businessHoursService;

    private int minLeadTimeHours;
    private int maxAdvanceDays;

    @Override
    public void initialize(ValidBookingTime constraintAnnotation) {
        this.minLeadTimeHours = constraintAnnotation.minLeadTimeHours();
        this.maxAdvanceDays = constraintAnnotation.maxAdvanceDays();
    }

    @Override
    public boolean isValid(BookingRequestDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getStartTime() == null) {
            return true; // Let @NotNull handle null cases
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = dto.getStartTime();

        // Check 1: Must be in the future
        if (startTime.isBefore(now)) {
            addConstraintViolation(context, "startTime", "Booking time must be in the future");
            return false;
        }

        // Check 2: Minimum lead time
        if (minLeadTimeHours > 0) {
            LocalDateTime minBookingTime = now.plusHours(minLeadTimeHours);
            if (startTime.isBefore(minBookingTime)) {
                addConstraintViolation(context, "startTime",
                        String.format("Booking requires at least %d hours advance notice", minLeadTimeHours));
                return false;
            }
        }

        // Check 3: Maximum advance booking
        if (maxAdvanceDays > 0) {
            LocalDateTime maxBookingTime = now.plusDays(maxAdvanceDays);
            if (startTime.isAfter(maxBookingTime)) {
                addConstraintViolation(context, "startTime",
                        String.format("Cannot book more than %d days in advance", maxAdvanceDays));
                return false;
            }
        }

        // Check 4: Within business hours (if tenant context is available)
        try {
            String tenantIdStr = TenantContext.getCurrentTenant();
            if (tenantIdStr != null) {
                UUID tenantId = UUID.fromString(tenantIdStr);
                if (!businessHoursService.isWithinBusinessHours(tenantId, startTime)) {
                    addConstraintViolation(context, "startTime", "Booking time is outside business hours");
                    return false;
                }
            }
        } catch (Exception e) {
            // If we can't check business hours, skip this validation
            log.debug("Could not validate business hours: {}", e.getMessage());
        }

        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String propertyNode, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(propertyNode)
                .addConstraintViolation();
    }
}
