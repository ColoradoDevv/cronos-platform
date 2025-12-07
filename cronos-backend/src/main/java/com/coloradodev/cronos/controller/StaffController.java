package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.core.tenant.TenantContext;
import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.domain.Staff;
import com.coloradodev.cronos.dto.booking.BookingResponseDTO;
import com.coloradodev.cronos.dto.mapper.BookingMapper;
import com.coloradodev.cronos.dto.mapper.ServiceMapper;
import com.coloradodev.cronos.dto.mapper.StaffMapper;
import com.coloradodev.cronos.dto.service.ServiceSummaryDTO;
import com.coloradodev.cronos.dto.staff.StaffRequestDTO;
import com.coloradodev.cronos.dto.staff.StaffResponseDTO;
import com.coloradodev.cronos.service.CalendarService;
import com.coloradodev.cronos.service.StaffService;
import com.coloradodev.cronos.service.StaffService.StaffAvailability;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for managing staff members.
 */
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;
    private final CalendarService calendarService;
    private final StaffMapper staffMapper;
    private final ServiceMapper serviceMapper;
    private final BookingMapper bookingMapper;

    // ==================== Staff CRUD ====================

    /**
     * Create a new staff member.
     * Note: Requires a userId - usually staff are created from existing users.
     */
    @PostMapping
    public ResponseEntity<StaffResponseDTO> createStaff(
            @Valid @RequestBody StaffRequestDTO request,
            @RequestParam UUID userId) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Staff staff = staffService.createStaff(tenantId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(staffMapper.toResponseDTO(staff));
    }

    /**
     * Get all staff for the current tenant.
     */
    @GetMapping
    public ResponseEntity<List<StaffResponseDTO>> getStaff(
            @RequestParam(required = false) Boolean active) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        List<Staff> staff;
        if (Boolean.TRUE.equals(active)) {
            staff = staffService.getActiveStaff(tenantId);
        } else {
            staff = staffService.getStaffByTenant(tenantId);
        }
        List<StaffResponseDTO> response = staff.stream()
                .map(staffMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Get a staff member by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> getStaffMember(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Staff staff = staffService.getStaffById(tenantId, id);
        return ResponseEntity.ok(staffMapper.toResponseDTO(staff));
    }

    /**
     * Update a staff member.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> updateStaff(
            @PathVariable UUID id,
            @Valid @RequestBody StaffRequestDTO request) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Staff staff = staffService.updateStaff(tenantId, id, request);
        return ResponseEntity.ok(staffMapper.toResponseDTO(staff));
    }

    /**
     * Delete (deactivate) a staff member.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStaff(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        staffService.deactivateStaff(tenantId, id);
    }

    // ==================== Staff Activation ====================

    /**
     * Activate a staff member.
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<StaffResponseDTO> activateStaff(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Staff staff = staffService.activateStaff(tenantId, id);
        return ResponseEntity.ok(staffMapper.toResponseDTO(staff));
    }

    /**
     * Deactivate a staff member.
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<StaffResponseDTO> deactivateStaff(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Staff staff = staffService.deactivateStaff(tenantId, id);
        return ResponseEntity.ok(staffMapper.toResponseDTO(staff));
    }

    // ==================== Service Assignment ====================

    /**
     * Get all services assigned to a staff member.
     */
    @GetMapping("/{id}/services")
    public ResponseEntity<Set<ServiceSummaryDTO>> getStaffServices(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        var services = staffService.getStaffServices(tenantId, id);
        Set<ServiceSummaryDTO> response = services.stream()
                .map(serviceMapper::toSummaryDTO)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(response);
    }

    /**
     * Assign a service to a staff member.
     */
    @PostMapping("/{id}/services/{serviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignServiceToStaff(
            @PathVariable UUID id,
            @PathVariable UUID serviceId) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        staffService.assignServiceToStaff(tenantId, id, serviceId);
    }

    /**
     * Remove a service from a staff member.
     */
    @DeleteMapping("/{id}/services/{serviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeServiceFromStaff(
            @PathVariable UUID id,
            @PathVariable UUID serviceId) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        staffService.removeServiceFromStaff(tenantId, id, serviceId);
    }

    // ==================== Availability & Schedule ====================

    /**
     * Get staff availability for a specific date.
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<StaffAvailability> getStaffAvailability(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        StaffAvailability availability = staffService.getStaffAvailability(tenantId, id, date);
        return ResponseEntity.ok(availability);
    }

    /**
     * Get staff schedule for a date range.
     */
    @GetMapping("/{id}/schedule")
    public ResponseEntity<List<BookingResponseDTO>> getStaffSchedule(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        List<Booking> bookings = calendarService.getStaffSchedule(tenantId, id, start, end);
        List<BookingResponseDTO> response = bookings.stream()
                .map(bookingMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }
}
