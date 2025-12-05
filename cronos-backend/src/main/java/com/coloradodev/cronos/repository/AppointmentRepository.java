package com.coloradodev.cronos.repository;

import com.coloradodev.cronos.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @Query("SELECT a FROM Appointment a WHERE a.service.id = :serviceId AND a.startTime < :endTime AND a.endTime > :startTime")
    List<Appointment> findOverlappingAppointments(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("serviceId") UUID serviceId);

    @Query("SELECT a FROM Appointment a WHERE a.service.id = :serviceId AND a.startTime >= :startOfDay AND a.endTime <= :endOfDay")
    List<Appointment> findByServiceIdAndDateRange(
            @Param("serviceId") UUID serviceId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);
}
