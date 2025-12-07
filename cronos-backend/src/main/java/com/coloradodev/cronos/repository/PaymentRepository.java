package com.coloradodev.cronos.repository;

import com.coloradodev.cronos.domain.Payment;
import com.coloradodev.cronos.domain.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByTenantId(UUID tenantId);

    Optional<Payment> findByTenantIdAndId(UUID tenantId, UUID id);

    List<Payment> findByTenantIdAndStatus(UUID tenantId, PaymentStatus status);

    List<Payment> findByTenantIdAndBookingId(UUID tenantId, UUID bookingId);

    List<Payment> findByTenantIdAndAppointmentId(UUID tenantId, UUID appointmentId);

    List<Payment> findByTenantIdAndPaidAtBetween(
            UUID tenantId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    Optional<Payment> findByBookingId(UUID bookingId);

    List<Payment> findAllByBookingId(UUID bookingId);
}
