package com.coloradodev.cronos.repository;

import com.coloradodev.cronos.domain.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, UUID> {

    List<ServiceCategory> findByTenantIdOrderByDisplayOrderAsc(UUID tenantId);

    Optional<ServiceCategory> findByTenantIdAndId(UUID tenantId, UUID id);

    Optional<ServiceCategory> findByTenantIdAndName(UUID tenantId, String name);
}
