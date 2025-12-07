package com.coloradodev.cronos.repository;

import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.domain.Booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByTenantId(UUID tenantId);

    Optional<Booking> findByTenantIdAndId(UUID tenantId, UUID id);

    List<Booking> findByTenantIdAndStatus(UUID tenantId, BookingStatus status);

    List<Booking> findByTenantIdAndClientId(UUID tenantId, UUID clientId);

    List<Booking> findByTenantIdAndServiceId(UUID tenantId, UUID serviceId);

    List<Booking> findByTenantIdAndStaffId(UUID tenantId, UUID staffId);

    @Query("SELECT b FROM Booking b WHERE b.tenantId = :tenantId " +
            "AND b.startTime >= :startDate AND b.endTime <= :endDate " +
            "ORDER BY b.startTime ASC")
    List<Booking> findByTenantIdAndDateRange(
            @Param("tenantId") UUID tenantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b FROM Booking b WHERE b.tenantId = :tenantId " +
            "AND b.staffId = :staffId " +
            "AND b.startTime >= :startDate AND b.endTime <= :endDate " +
            "AND b.status IN ('PENDING', 'CONFIRMED')")
    List<Booking> findActiveBookingsForStaffInRange(
            @Param("tenantId") UUID tenantId,
            @Param("staffId") UUID staffId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
