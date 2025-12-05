package com.coloradodev.cronos.domain;

public interface TenantAware {
    void setTenant(Tenant tenant);

    Tenant getTenant();
}
