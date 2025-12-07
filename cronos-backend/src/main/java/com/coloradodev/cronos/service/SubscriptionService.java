package com.coloradodev.cronos.service;

import com.coloradodev.cronos.domain.Subscription;
import com.coloradodev.cronos.domain.Subscription.SubscriptionPlan;
import com.coloradodev.cronos.domain.Subscription.SubscriptionStatus;
import com.coloradodev.cronos.exception.SubscriptionLimitExceededException;
import com.coloradodev.cronos.exception.ResourceNotFoundException;
import com.coloradodev.cronos.repository.SubscriptionRepository;
import com.coloradodev.cronos.repository.StaffRepository;
import com.coloradodev.cronos.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing tenant subscriptions, limits, and feature flags.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final StaffRepository staffRepository;
    private final ServiceRepository serviceRepository;

    // Default limits per plan
    private static final Map<SubscriptionPlan, PlanLimits> PLAN_LIMITS = Map.of(
            SubscriptionPlan.FREE, new PlanLimits(2, 5, 50),
            SubscriptionPlan.BASIC, new PlanLimits(5, 20, 200),
            SubscriptionPlan.PRO, new PlanLimits(15, 50, 1000),
            SubscriptionPlan.ENTERPRISE, new PlanLimits(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));

    /**
     * Create a new subscription for a tenant.
     */
    @Transactional
    public Subscription createSubscription(UUID tenantId, SubscriptionPlan plan) {
        // Check if subscription already exists
        if (subscriptionRepository.findByTenantId(tenantId).isPresent()) {
            throw new IllegalStateException("Subscription already exists for tenant: " + tenantId);
        }

        PlanLimits limits = PLAN_LIMITS.get(plan);

        Subscription subscription = new Subscription();
        subscription.setTenantId(tenantId);
        subscription.setPlan(plan);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDate.now());
        subscription.setMaxStaff(limits.maxStaff());
        subscription.setMaxServices(limits.maxServices());
        subscription.setMaxAppointmentsPerMonth(limits.maxAppointmentsPerMonth());
        subscription.setFeatures(getDefaultFeatures(plan));

        Subscription saved = subscriptionRepository.save(subscription);
        log.info("Created {} subscription for tenant {}", plan, tenantId);

        return saved;
    }

    /**
     * Update a tenant's subscription plan.
     */
    @Transactional
    public Subscription updateSubscription(UUID tenantId, SubscriptionPlan newPlan) {
        Subscription subscription = getSubscription(tenantId);

        SubscriptionPlan oldPlan = subscription.getPlan();
        PlanLimits limits = PLAN_LIMITS.get(newPlan);

        subscription.setPlan(newPlan);
        subscription.setMaxStaff(limits.maxStaff());
        subscription.setMaxServices(limits.maxServices());
        subscription.setMaxAppointmentsPerMonth(limits.maxAppointmentsPerMonth());
        subscription.setFeatures(getDefaultFeatures(newPlan));

        Subscription saved = subscriptionRepository.save(subscription);
        log.info("Updated tenant {} subscription from {} to {}", tenantId, oldPlan, newPlan);

        return saved;
    }

    /**
     * Cancel a tenant's subscription.
     */
    @Transactional
    public Subscription cancelSubscription(UUID tenantId) {
        Subscription subscription = getSubscription(tenantId);

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setEndDate(LocalDate.now());

        Subscription saved = subscriptionRepository.save(subscription);
        log.info("Cancelled subscription for tenant {}", tenantId);

        return saved;
    }

    /**
     * Get a tenant's subscription.
     */
    @Transactional(readOnly = true)
    public Subscription getSubscription(UUID tenantId) {
        return subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", tenantId.toString()));
    }

    /**
     * Check if a limit would be exceeded.
     * 
     * @param tenantId  The tenant to check
     * @param limitType The type of limit (STAFF, SERVICES, APPOINTMENTS)
     * @return true if within limits, false if would exceed
     */
    @Transactional(readOnly = true)
    public boolean checkLimit(UUID tenantId, LimitType limitType) {
        Subscription subscription = getSubscription(tenantId);

        return switch (limitType) {
            case STAFF -> {
                int currentStaff = staffRepository.findByTenantId(tenantId).size();
                yield currentStaff < subscription.getMaxStaff();
            }
            case SERVICES -> {
                int currentServices = serviceRepository.findByTenantId(tenantId).size();
                yield currentServices < subscription.getMaxServices();
            }
            case APPOINTMENTS -> {
                // For appointments, this would need to count current month's appointments
                // Returning true for now - implement with booking repository later
                yield true;
            }
        };
    }

    /**
     * Validate a limit and throw exception if exceeded.
     */
    @Transactional(readOnly = true)
    public void validateLimit(UUID tenantId, LimitType limitType) {
        Subscription subscription = getSubscription(tenantId);

        int currentValue = switch (limitType) {
            case STAFF -> staffRepository.findByTenantId(tenantId).size();
            case SERVICES -> serviceRepository.findByTenantId(tenantId).size();
            case APPOINTMENTS -> 0; // Implement later
        };

        int maxValue = switch (limitType) {
            case STAFF -> subscription.getMaxStaff();
            case SERVICES -> subscription.getMaxServices();
            case APPOINTMENTS -> subscription.getMaxAppointmentsPerMonth();
        };

        if (currentValue >= maxValue) {
            throw new SubscriptionLimitExceededException(limitType.name(), currentValue, maxValue);
        }
    }

    /**
     * Check if a feature is enabled for a tenant.
     */
    @Transactional(readOnly = true)
    public boolean isFeatureEnabled(UUID tenantId, String featureName) {
        Subscription subscription = getSubscription(tenantId);

        if (subscription.getFeatures() == null) {
            return false;
        }

        Object value = subscription.getFeatures().get(featureName);
        return value instanceof Boolean && (Boolean) value;
    }

    /**
     * Check if tenant has an active subscription.
     */
    @Transactional(readOnly = true)
    public boolean hasActiveSubscription(UUID tenantId) {
        return subscriptionRepository.findByTenantIdAndStatus(tenantId, SubscriptionStatus.ACTIVE)
                .isPresent();
    }

    private Map<String, Object> getDefaultFeatures(SubscriptionPlan plan) {
        Map<String, Object> features = new HashMap<>();

        switch (plan) {
            case ENTERPRISE:
                features.put("customBranding", true);
                features.put("apiAccess", true);
                features.put("prioritySupport", true);
                // Fall through
            case PRO:
                features.put("smsNotifications", true);
                features.put("advancedReporting", true);
                features.put("multiLocation", true);
                // Fall through
            case BASIC:
                features.put("emailNotifications", true);
                features.put("calendarSync", true);
                features.put("onlineBooking", true);
                // Fall through
            case FREE:
                features.put("basicBooking", true);
                features.put("clientManagement", true);
                break;
        }

        return features;
    }

    public enum LimitType {
        STAFF,
        SERVICES,
        APPOINTMENTS
    }

    private record PlanLimits(int maxStaff, int maxServices, int maxAppointmentsPerMonth) {
    }
}
