package com.coloradodev.cronos.core.tenant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static void setCurrentTenant(String tenantId) {
        log.debug("Setting current tenant to {}", tenantId);
        CURRENT_TENANT.set(tenantId);
    }

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        log.debug("Clearing current tenant");
        CURRENT_TENANT.remove();
    }

    /**
     * Get the current tenant ID as a UUID.
     * 
     * @return The current tenant's UUID
     * @throws IllegalStateException if no tenant context is set
     */
    public static java.util.UUID getCurrentTenantId() {
        String tenantId = getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("No tenant context available");
        }
        return java.util.UUID.fromString(tenantId);
    }
}
