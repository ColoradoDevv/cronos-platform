package com.coloradodev.cronos.repository;

import com.coloradodev.cronos.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByTenantId(UUID tenantId);

    List<AuditLog> findByTenantIdAndUserId(UUID tenantId, UUID userId);

    List<AuditLog> findByTenantIdAndEntityTypeAndEntityId(
            UUID tenantId,
            String entityType,
            UUID entityId);

    @Query("SELECT a FROM AuditLog a WHERE a.tenantId = :tenantId " +
            "AND a.timestamp >= :startDate AND a.timestamp <= :endDate " +
            "ORDER BY a.timestamp DESC")
    List<AuditLog> findByTenantIdAndTimestampRange(
            @Param("tenantId") UUID tenantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE a.tenantId = :tenantId " +
            "AND a.action = :action " +
            "AND a.timestamp >= :startDate AND a.timestamp <= :endDate " +
            "ORDER BY a.timestamp DESC")
    List<AuditLog> findByTenantIdAndActionAndTimestampBetween(
            @Param("tenantId") UUID tenantId,
            @Param("action") String action,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
