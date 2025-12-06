package com.coloradodev.cronos.repository;

import com.coloradodev.cronos.domain.BusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessHoursRepository extends JpaRepository<BusinessHours, UUID> {

    List<BusinessHours> findByTenantId(UUID tenantId);

    Optional<BusinessHours> findByTenantIdAndDayOfWeek(UUID tenantId, DayOfWeek dayOfWeek);

    List<BusinessHours> findByTenantIdAndIsOpen(UUID tenantId, Boolean isOpen);
}
