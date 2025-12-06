package com.coloradodev.cronos.domain;

import java.util.UUID;

public interface TenantAware {
    UUID getTenantId();

    void setTenantId(UUID tenantId);
}
