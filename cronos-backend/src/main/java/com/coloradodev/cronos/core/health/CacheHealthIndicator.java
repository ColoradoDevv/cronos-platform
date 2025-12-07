package com.coloradodev.cronos.core.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * Health indicator for the cache system.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheHealthIndicator implements HealthIndicator {

    private final CacheManager cacheManager;

    @Override
    public Health health() {
        try {
            var cacheNames = cacheManager.getCacheNames();
            long cacheCount = cacheNames.spliterator().getExactSizeIfKnown();

            if (cacheCount < 0) {
                // Count manually if exact size not known
                cacheCount = 0;
                for (String name : cacheNames) {
                    cacheCount++;
                }
            }

            return Health.up()
                    .withDetail("service", "Caffeine Cache")
                    .withDetail("caches", cacheNames)
                    .withDetail("cacheCount", cacheCount)
                    .build();
        } catch (Exception e) {
            log.error("Cache health check failed", e);
            return Health.down()
                    .withDetail("service", "Caffeine Cache")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
