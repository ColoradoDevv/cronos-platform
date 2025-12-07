package com.coloradodev.cronos.dto.businesshours;

import com.coloradodev.cronos.validation.ValidTimeRange;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Request DTO for creating/updating BusinessHours.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidTimeRange(startField = "openTime", endField = "closeTime", message = "Close time must be after open time")
public class BusinessHoursRequestDTO {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    private LocalTime openTime;

    private LocalTime closeTime;

    private Boolean isOpen;

    private Boolean isClosed;
}
