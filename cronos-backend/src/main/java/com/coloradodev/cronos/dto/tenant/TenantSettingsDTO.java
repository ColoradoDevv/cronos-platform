package com.coloradodev.cronos.dto.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Map;

/**
 * Request DTO for tenant settings.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSettingsDTO {

    private LocalTime workDayStart;

    private LocalTime workDayEnd;

    private String timezone;

    private String currency;

    private String dateFormat;

    private String timeFormat;

    private Boolean emailNotificationsEnabled;

    private Boolean smsNotificationsEnabled;

    private Integer bookingLeadTimeHours;

    private Integer cancellationLeadTimeHours;

    private Map<String, Object> customSettings;
}
