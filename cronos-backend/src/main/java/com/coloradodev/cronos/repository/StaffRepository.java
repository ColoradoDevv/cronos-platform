package com.coloradodev.cronos.repository;

import com.coloradodev.cronos.domain.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {

    List<Staff> findByTenantId(UUID tenantId);

    Optional<Staff> findByTenantIdAndId(UUID tenantId, UUID id);

    List<Staff> findByTenantIdAndIsActive(UUID tenantId, Boolean isActive);

    Optional<Staff> findByTenantIdAndUserId(UUID tenantId, UUID userId);
}
