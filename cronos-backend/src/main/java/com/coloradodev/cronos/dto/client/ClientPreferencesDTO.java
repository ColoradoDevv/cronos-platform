package com.coloradodev.cronos.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for client preferences.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientPreferencesDTO {
    private String communicationChannel;
    private Boolean reminderEnabled;
    private Integer reminderHours;
    private String language;
    private String timezone;
    private String notes;
}
