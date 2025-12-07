package com.coloradodev.cronos.dto.tenant;

import com.coloradodev.cronos.dto.subscription.SubscriptionResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Response DTO for Tenant entity with full details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponseDTO {

    private UUID id;
    private String name;
    private String slug;
    private String status;
    private String primaryColor;
    private String logoUrl;
    private LocalTime workDayStart;
    private LocalTime workDayEnd;
    private SubscriptionResponseDTO subscription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
