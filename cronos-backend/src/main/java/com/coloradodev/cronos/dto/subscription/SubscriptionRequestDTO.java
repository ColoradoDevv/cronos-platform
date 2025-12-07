package com.coloradodev.cronos.dto.subscription;

import com.coloradodev.cronos.domain.Subscription.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * Request DTO for creating/updating a Subscription.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestDTO {

    @NotNull(message = "Subscription plan is required")
    private SubscriptionPlan plan;

    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private Integer maxStaff;
    
    private Integer maxServices;
    
    private Integer maxAppointmentsPerMonth;
    
    private Map<String, Object> features;
}
