package com.coloradodev.cronos.core.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health indicator for the Resend email service.
 */
@Slf4j
@Component
public class EmailHealthIndicator implements HealthIndicator {

    @Value("${resend.api-key:}")
    private String apiKey;

    @Override
    public Health health() {
        if (apiKey == null || apiKey.isEmpty()) {
            return Health.unknown()
                    .withDetail("service", "Resend Email")
                    .withDetail("status", "API key not configured")
                    .build();
        }

        // In production, you could make a test API call to verify connectivity
        // For now, just check if API key is present
        return Health.up()
                .withDetail("service", "Resend Email")
                .withDetail("status", "API key configured")
                .build();
    }
}
