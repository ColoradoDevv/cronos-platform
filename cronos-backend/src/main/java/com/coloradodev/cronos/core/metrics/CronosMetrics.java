package com.coloradodev.cronos.core.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom metrics for business operations.
 */
@Component
@RequiredArgsConstructor
public class CronosMetrics {

    private final MeterRegistry meterRegistry;

    // Counters for different operations
    private Counter bookingsCreatedCounter;
    private Counter bookingsCancelledCounter;
    private Counter appointmentsCompletedCounter;
    private Counter tenantsRegisteredCounter;
    private Counter clientsRegisteredCounter;

    // Timer for API response times
    private Timer apiResponseTimer;

    // Tenant-specific counters (lazy-loaded)
    private final ConcurrentHashMap<UUID, Counter> tenantBookingCounters = new ConcurrentHashMap<>();

    @jakarta.annotation.PostConstruct
    public void init() {
        // Initialize counters
        bookingsCreatedCounter = Counter.builder("cronos.bookings.created")
                .description("Total bookings created")
                .register(meterRegistry);

        bookingsCancelledCounter = Counter.builder("cronos.bookings.cancelled")
                .description("Total bookings cancelled")
                .register(meterRegistry);

        appointmentsCompletedCounter = Counter.builder("cronos.appointments.completed")
                .description("Total appointments completed")
                .register(meterRegistry);

        tenantsRegisteredCounter = Counter.builder("cronos.tenants.registered")
                .description("Total tenants registered")
                .register(meterRegistry);

        clientsRegisteredCounter = Counter.builder("cronos.clients.registered")
                .description("Total clients registered")
                .register(meterRegistry);

        apiResponseTimer = Timer.builder("cronos.api.response.time")
                .description("API response time")
                .register(meterRegistry);
    }

    // ==================== Booking Metrics ====================

    public void recordBookingCreated() {
        bookingsCreatedCounter.increment();
    }

    public void recordBookingCreated(UUID tenantId) {
        bookingsCreatedCounter.increment();
        getTenantBookingCounter(tenantId).increment();
    }

    public void recordBookingCancelled() {
        bookingsCancelledCounter.increment();
    }

    // ==================== Appointment Metrics ====================

    public void recordAppointmentCompleted() {
        appointmentsCompletedCounter.increment();
    }

    // ==================== Tenant/Client Metrics ====================

    public void recordTenantRegistered() {
        tenantsRegisteredCounter.increment();
    }

    public void recordClientRegistered() {
        clientsRegisteredCounter.increment();
    }

    // ==================== API Metrics ====================

    public Timer.Sample startApiTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopApiTimer(Timer.Sample sample) {
        sample.stop(apiResponseTimer);
    }

    // ==================== Helper Methods ====================

    private Counter getTenantBookingCounter(UUID tenantId) {
        return tenantBookingCounters.computeIfAbsent(tenantId, id -> Counter.builder("cronos.bookings.by_tenant")
                .tag("tenant_id", tenantId.toString())
                .description("Bookings per tenant")
                .register(meterRegistry));
    }
}
