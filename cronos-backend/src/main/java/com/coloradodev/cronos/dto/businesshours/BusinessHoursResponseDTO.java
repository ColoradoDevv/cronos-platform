package com.coloradodev.cronos.dto.businesshours;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Response DTO for BusinessHours entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessHoursResponseDTO {

    private UUID id;
    private DayOfWeek dayOfWeek;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean isOpen;
    private Boolean isClosed;
}
