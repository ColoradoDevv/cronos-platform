package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.core.tenant.TenantContext;
import com.coloradodev.cronos.domain.Subscription;
import com.coloradodev.cronos.domain.Subscription.SubscriptionPlan;
import com.coloradodev.cronos.dto.mapper.SubscriptionMapper;
import com.coloradodev.cronos.dto.subscription.SubscriptionResponseDTO;
import com.coloradodev.cronos.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for subscription management.
 */
@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionMapper subscriptionMapper;

    /**
     * Get the current tenant's subscription.
     */
    @GetMapping
    public ResponseEntity<SubscriptionResponseDTO> getSubscription() {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Subscription subscription = subscriptionService.getSubscription(tenantId);
        return ResponseEntity.ok(subscriptionMapper.toResponseDTO(subscription));
    }

    /**
     * Upgrade to a new subscription plan.
     */
    @PostMapping("/upgrade")
    public ResponseEntity<SubscriptionResponseDTO> upgradePlan(
            @RequestBody Map<String, String> body) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        String planName = body.get("plan");
        SubscriptionPlan newPlan = SubscriptionPlan.valueOf(planName.toUpperCase());

        Subscription subscription = subscriptionService.updateSubscription(tenantId, newPlan);
        return ResponseEntity.ok(subscriptionMapper.toResponseDTO(subscription));
    }

    /**
     * Cancel the subscription.
     */
    @PostMapping("/cancel")
    public ResponseEntity<SubscriptionResponseDTO> cancelSubscription() {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Subscription subscription = subscriptionService.cancelSubscription(tenantId);
        return ResponseEntity.ok(subscriptionMapper.toResponseDTO(subscription));
    }

    /**
     * Get subscription usage statistics.
     */
    @GetMapping("/usage")
    public ResponseEntity<Map<String, Object>> getUsage() {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Subscription subscription = subscriptionService.getSubscription(tenantId);

        // Get current usage counts
        boolean staffLimit = subscriptionService.checkLimit(tenantId, SubscriptionService.LimitType.STAFF);
        boolean servicesLimit = subscriptionService.checkLimit(tenantId, SubscriptionService.LimitType.SERVICES);
        boolean appointmentsLimit = subscriptionService.checkLimit(tenantId,
                SubscriptionService.LimitType.APPOINTMENTS);

        Map<String, Object> usage = new HashMap<>();
        usage.put("plan", subscription.getPlan());
        usage.put("limits", Map.of(
                "maxStaff", subscription.getMaxStaff(),
                "maxServices", subscription.getMaxServices(),
                "maxAppointmentsPerMonth", subscription.getMaxAppointmentsPerMonth()));
        usage.put("withinLimits", Map.of(
                "staff", staffLimit,
                "services", servicesLimit,
                "appointments", appointmentsLimit));
        usage.put("features", subscription.getFeatures());

        return ResponseEntity.ok(usage);
    }
}
