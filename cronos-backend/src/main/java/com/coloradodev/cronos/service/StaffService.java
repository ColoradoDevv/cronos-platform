package com.coloradodev.cronos.service;

import com.coloradodev.cronos.domain.Service;
import com.coloradodev.cronos.domain.Staff;
import com.coloradodev.cronos.dto.staff.StaffRequestDTO;
import com.coloradodev.cronos.exception.ResourceNotFoundException;
import com.coloradodev.cronos.repository.ServiceRepository;
import com.coloradodev.cronos.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Service for managing staff members within a tenant.
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class StaffService {

    private final StaffRepository staffRepository;
    private final ServiceRepository serviceRepository;
    private final BusinessHoursService businessHoursService;
    private final AuditService auditService;
    private final SubscriptionService subscriptionService;

    /**
     * Create a new staff member for a tenant.
     */
    @Transactional
    public Staff createStaff(UUID tenantId, UUID userId, StaffRequestDTO request) {
        // Check subscription limits
        subscriptionService.validateLimit(tenantId, SubscriptionService.LimitType.STAFF);

        Staff staff = new Staff();
        staff.setTenantId(tenantId);
        staff.setUserId(userId);
        staff.setPosition(request.getPosition());
        staff.setBio(request.getBio());
        staff.setPhotoUrl(request.getPhotoUrl());
        staff.setIsActive(true);

        Staff saved = staffRepository.save(staff);

        auditService.logCreate(tenantId, userId, "Staff", saved.getId(),
                Map.of("position", request.getPosition() != null ? request.getPosition() : ""));

        log.info("Created staff {} for tenant {}", saved.getId(), tenantId);
        return saved;
    }

    /**
     * Update an existing staff member.
     */
    @Transactional
    public Staff updateStaff(UUID tenantId, UUID staffId, StaffRequestDTO request) {
        Staff staff = getStaffById(tenantId, staffId);

        Map<String, Object> oldValues = Map.of(
                "position", staff.getPosition() != null ? staff.getPosition() : "",
                "bio", staff.getBio() != null ? staff.getBio() : "");

        if (request.getPosition() != null) {
            staff.setPosition(request.getPosition());
        }
        if (request.getBio() != null) {
            staff.setBio(request.getBio());
        }
        if (request.getPhotoUrl() != null) {
            staff.setPhotoUrl(request.getPhotoUrl());
        }

        Staff saved = staffRepository.save(staff);

        Map<String, Object> newValues = Map.of(
                "position", saved.getPosition() != null ? saved.getPosition() : "",
                "bio", saved.getBio() != null ? saved.getBio() : "");

        auditService.logUpdate(tenantId, null, "Staff", saved.getId(), oldValues, newValues);

        log.info("Updated staff {} for tenant {}", staffId, tenantId);
        return saved;
    }

    /**
     * Get a staff member by ID.
     */
    @Transactional(readOnly = true)
    public Staff getStaffById(UUID tenantId, UUID staffId) {
        return staffRepository.findByTenantIdAndId(tenantId, staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", staffId.toString()));
    }

    /**
     * Get staff by user ID.
     */
    @Transactional(readOnly = true)
    public Staff getStaffByUserId(UUID tenantId, UUID userId) {
        return staffRepository.findByTenantIdAndUserId(tenantId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff with user", userId.toString()));
    }

    /**
     * Get all staff members for a tenant.
     */
    @Transactional(readOnly = true)
    public List<Staff> getStaffByTenant(UUID tenantId) {
        return staffRepository.findByTenantId(tenantId);
    }

    /**
     * Get active staff members for a tenant.
     */
    @Transactional(readOnly = true)
    public List<Staff> getActiveStaff(UUID tenantId) {
        return staffRepository.findByTenantIdAndIsActive(tenantId, true);
    }

    /**
     * Assign a service to a staff member.
     */
    @Transactional
    public void assignServiceToStaff(UUID tenantId, UUID staffId, UUID serviceId) {
        Staff staff = getStaffById(tenantId, staffId);
        Service service = serviceRepository.findByTenantIdAndId(tenantId, serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service", serviceId.toString()));

        staff.getServices().add(service);
        staffRepository.save(staff);

        auditService.logAction(tenantId, null, "ASSIGN_SERVICE", "Staff", staffId,
                null, Map.of("serviceId", serviceId, "serviceName", service.getName()));

        log.info("Assigned service {} to staff {} for tenant {}", serviceId, staffId, tenantId);
    }

    /**
     * Remove a service from a staff member.
     */
    @Transactional
    public void removeServiceFromStaff(UUID tenantId, UUID staffId, UUID serviceId) {
        Staff staff = getStaffById(tenantId, staffId);
        Service service = serviceRepository.findByTenantIdAndId(tenantId, serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service", serviceId.toString()));

        staff.getServices().remove(service);
        staffRepository.save(staff);

        auditService.logAction(tenantId, null, "REMOVE_SERVICE", "Staff", staffId,
                Map.of("serviceId", serviceId), null);

        log.info("Removed service {} from staff {} for tenant {}", serviceId, staffId, tenantId);
    }

    /**
     * Get all services assigned to a staff member.
     */
    @Transactional(readOnly = true)
    public Set<Service> getStaffServices(UUID tenantId, UUID staffId) {
        Staff staff = getStaffById(tenantId, staffId);
        return staff.getServices();
    }

    /**
     * Check staff availability for a given date.
     * Returns available time slots based on business hours.
     */
    @Transactional(readOnly = true)
    public StaffAvailability getStaffAvailability(UUID tenantId, UUID staffId, LocalDate date) {
        Staff staff = getStaffById(tenantId, staffId);

        if (!staff.getIsActive()) {
            return new StaffAvailability(staffId, date, false, List.of());
        }

        // Get business hours for the day
        var businessHours = businessHoursService.getBusinessHoursForDate(tenantId, date);

        if (businessHours.isEmpty() || !businessHours.get().getIsOpen()) {
            return new StaffAvailability(staffId, date, false, List.of());
        }

        var hours = businessHours.get();
        LocalDateTime startOfDay = LocalDateTime.of(date, hours.getOpenTime());
        LocalDateTime endOfDay = LocalDateTime.of(date, hours.getCloseTime());

        // For now, return full business hours as available
        // In a full implementation, this would subtract existing bookings
        TimeSlotRange availableRange = new TimeSlotRange(
                startOfDay.toLocalTime(),
                endOfDay.toLocalTime());

        return new StaffAvailability(staffId, date, true, List.of(availableRange));
    }

    /**
     * Deactivate a staff member.
     */
    @Transactional
    public Staff deactivateStaff(UUID tenantId, UUID staffId) {
        Staff staff = getStaffById(tenantId, staffId);

        staff.setIsActive(false);
        Staff saved = staffRepository.save(staff);

        auditService.logAction(tenantId, null, "DEACTIVATE", "Staff", staffId);

        log.info("Deactivated staff {} for tenant {}", staffId, tenantId);
        return saved;
    }

    /**
     * Reactivate a staff member.
     */
    @Transactional
    public Staff activateStaff(UUID tenantId, UUID staffId) {
        Staff staff = getStaffById(tenantId, staffId);

        staff.setIsActive(true);
        Staff saved = staffRepository.save(staff);

        auditService.logAction(tenantId, null, "ACTIVATE", "Staff", staffId);

        log.info("Activated staff {} for tenant {}", staffId, tenantId);
        return saved;
    }

    /**
     * Get staff members who can provide a specific service.
     */
    @Transactional(readOnly = true)
    public List<Staff> getStaffForService(UUID tenantId, UUID serviceId) {
        return staffRepository.findByTenantIdAndServiceId(tenantId, serviceId);
    }

    // DTOs for availability response
    public record StaffAvailability(
            UUID staffId,
            LocalDate date,
            boolean available,
            List<TimeSlotRange> availableSlots) {
    }

    public record TimeSlotRange(
            LocalTime startTime,
            LocalTime endTime) {
    }
}
