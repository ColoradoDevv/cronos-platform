package com.coloradodev.cronos.repository;

import com.coloradodev.cronos.domain.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {

    List<Staff> findByTenantId(UUID tenantId);

    Optional<Staff> findByTenantIdAndId(UUID tenantId, UUID id);

    List<Staff> findByTenantIdAndIsActive(UUID tenantId, Boolean isActive);

    Optional<Staff> findByTenantIdAndUserId(UUID tenantId, UUID userId);

    @Query("SELECT s FROM Staff s JOIN s.services srv WHERE srv.id = :serviceId AND s.isActive = true")
    List<Staff> findByServiceId(@Param("serviceId") UUID serviceId);

    @Query("SELECT s FROM Staff s JOIN s.services srv WHERE s.tenantId = :tenantId AND srv.id = :serviceId AND s.isActive = true")
    List<Staff> findByTenantIdAndServiceId(@Param("tenantId") UUID tenantId, @Param("serviceId") UUID serviceId);
}
