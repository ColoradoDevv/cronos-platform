package com.coloradodev.cronos.repository;

import com.coloradodev.cronos.domain.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {

    List<Service> findByTenantId(UUID tenantId);

    Optional<Service> findByTenantIdAndId(UUID tenantId, UUID id);

    List<Service> findByTenantIdAndCategoryId(UUID tenantId, UUID categoryId);

    List<Service> findByTenantIdAndIsActive(UUID tenantId, Boolean isActive);

    List<Service> findByTenantIdAndIsActiveOrderByNameAsc(UUID tenantId, Boolean isActive);
}
