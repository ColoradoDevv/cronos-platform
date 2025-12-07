package com.coloradodev.cronos.dto.subscription;

import com.coloradodev.cronos.domain.Subscription.SubscriptionPlan;
import com.coloradodev.cronos.domain.Subscription.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for Subscription entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponseDTO {

    private UUID id;
    private UUID tenantId;
    private SubscriptionPlan plan;
    private SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxStaff;
    private Integer maxServices;
    private Integer maxAppointmentsPerMonth;
    private Map<String, Object> features;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
