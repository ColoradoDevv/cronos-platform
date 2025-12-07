package com.coloradodev.cronos.service;

import com.coloradodev.cronos.domain.AuditLog;
import com.coloradodev.cronos.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for centralized audit logging of all tenant actions.
 * Provides traceability for compliance and debugging purposes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Log an action performed by a user.
     *
     * @param tenantId   The tenant context
     * @param userId     The user performing the action
     * @param action     The action type (e.g., CREATE, UPDATE, DELETE)
     * @param entityType The type of entity affected (e.g., "Booking", "Client")
     * @param entityId   The ID of the affected entity
     * @param oldValue   Previous state of the entity (for UPDATE/DELETE)
     * @param newValue   New state of the entity (for CREATE/UPDATE)
     * @return The created audit log entry
     */
    @Transactional
    public AuditLog logAction(UUID tenantId, UUID userId, String action,
            String entityType, UUID entityId,
            Map<String, Object> oldValue, Map<String, Object> newValue) {

        AuditLog auditLog = new AuditLog();
        auditLog.setTenantId(tenantId);
        auditLog.setUserId(userId);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);
        auditLog.setTimestamp(LocalDateTime.now());

        AuditLog saved = auditLogRepository.save(auditLog);
        log.debug("Audit log created: {} {} on {} by user {}", action, entityType, entityId, userId);

        return saved;
    }

    /**
     * Simplified log action without old/new values.
     */
    @Transactional
    public AuditLog logAction(UUID tenantId, UUID userId, String action,
            String entityType, UUID entityId) {
        return logAction(tenantId, userId, action, entityType, entityId, null, null);
    }

    /**
     * Log a CREATE action with new value only.
     */
    @Transactional
    public AuditLog logCreate(UUID tenantId, UUID userId, String entityType,
            UUID entityId, Map<String, Object> newValue) {
        return logAction(tenantId, userId, "CREATE", entityType, entityId, null, newValue);
    }

    /**
     * Log an UPDATE action with old and new values.
     */
    @Transactional
    public AuditLog logUpdate(UUID tenantId, UUID userId, String entityType,
            UUID entityId, Map<String, Object> oldValue, Map<String, Object> newValue) {
        return logAction(tenantId, userId, "UPDATE", entityType, entityId, oldValue, newValue);
    }

    /**
     * Log a DELETE action with old value only.
     */
    @Transactional
    public AuditLog logDelete(UUID tenantId, UUID userId, String entityType,
            UUID entityId, Map<String, Object> oldValue) {
        return logAction(tenantId, userId, "DELETE", entityType, entityId, oldValue, null);
    }

    /**
     * Get audit logs for a tenant with optional filtering.
     *
     * @param tenantId  The tenant to query
     * @param action    Optional action filter
     * @param startDate Optional start date filter
     * @param endDate   Optional end date filter
     * @param pageable  Pagination parameters
     * @return Paginated list of audit logs
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogs(UUID tenantId, String action,
            LocalDateTime startDate, LocalDateTime endDate,
            Pageable pageable) {
        List<AuditLog> logs;

        if (action != null && startDate != null && endDate != null) {
            logs = auditLogRepository.findByTenantIdAndActionAndTimestampBetween(
                    tenantId, action, startDate, endDate);
        } else if (startDate != null && endDate != null) {
            logs = auditLogRepository.findByTenantIdAndTimestampRange(
                    tenantId, startDate, endDate);
        } else {
            logs = auditLogRepository.findByTenantId(tenantId);
        }

        // Apply pagination manually (for now - could be optimized with custom queries)
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), logs.size());

        if (start > logs.size()) {
            return new PageImpl<>(List.of(), pageable, logs.size());
        }

        return new PageImpl<>(logs.subList(start, end), pageable, logs.size());
    }

    /**
     * Get the complete change history for a specific entity.
     *
     * @param tenantId   The tenant context
     * @param entityType The type of entity
     * @param entityId   The entity ID
     * @return List of all audit logs for this entity, ordered by timestamp
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getEntityHistory(UUID tenantId, String entityType, UUID entityId) {
        return auditLogRepository.findByTenantIdAndEntityTypeAndEntityId(
                tenantId, entityType, entityId);
    }

    /**
     * Get all actions performed by a specific user.
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getUserActions(UUID tenantId, UUID userId) {
        return auditLogRepository.findByTenantIdAndUserId(tenantId, userId);
    }
}
