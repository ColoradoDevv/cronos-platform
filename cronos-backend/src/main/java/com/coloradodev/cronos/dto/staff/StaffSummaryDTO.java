package com.coloradodev.cronos.dto.staff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Lightweight summary DTO for Staff (for use in nested references).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffSummaryDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String position;
    private String photoUrl;
}
