package com.coloradodev.cronos.domain;

import com.coloradodev.cronos.core.tenant.TenantContext;
import jakarta.persistence.PrePersist;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class TenantEntityListener {

    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof TenantAware tenantAware) {
            String tenantId = TenantContext.getCurrentTenant();
            if (tenantId != null) {
                log.debug("Setting tenant {} for entity {}", tenantId, entity.getClass().getSimpleName());
                tenantAware.setTenantId(UUID.fromString(tenantId));
            } else {
                log.warn("No tenant found in context for entity {}", entity.getClass().getSimpleName());
                // Depending on requirements, we might want to throw an exception here
                // throw new IllegalStateException("No tenant context found");
            }
        }
    }
}
