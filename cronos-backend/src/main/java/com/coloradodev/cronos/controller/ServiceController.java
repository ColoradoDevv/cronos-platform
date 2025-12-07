package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.core.tenant.TenantContext;
import com.coloradodev.cronos.domain.Service;
import com.coloradodev.cronos.domain.ServiceCategory;
import com.coloradodev.cronos.domain.Staff;
import com.coloradodev.cronos.dto.category.ServiceCategoryRequestDTO;
import com.coloradodev.cronos.dto.category.ServiceCategoryResponseDTO;
import com.coloradodev.cronos.dto.mapper.ServiceCategoryMapper;
import com.coloradodev.cronos.dto.mapper.ServiceMapper;
import com.coloradodev.cronos.dto.mapper.StaffMapper;
import com.coloradodev.cronos.dto.service.ServiceRequestDTO;
import com.coloradodev.cronos.dto.service.ServiceResponseDTO;
import com.coloradodev.cronos.dto.staff.StaffResponseDTO;
import com.coloradodev.cronos.repository.ServiceCategoryRepository;
import com.coloradodev.cronos.service.ServiceManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing services (business offerings).
 * 
 * Authorization:
 * - Read operations: Any authenticated user
 * - Create/Update/Delete: ADMIN role required
 */
@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceManagementService serviceManagementService;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ServiceMapper serviceMapper;
    private final ServiceCategoryMapper serviceCategoryMapper;
    private final StaffMapper staffMapper;

    // ==================== Service CRUD ====================

    /**
     * Create a new service.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponseDTO> createService(
            @Valid @RequestBody ServiceRequestDTO request) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Service service = serviceManagementService.createService(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceMapper.toResponseDTO(service));
    }

    /**
     * Get all services for the current tenant.
     */
    @GetMapping
    public ResponseEntity<List<ServiceResponseDTO>> getServices(
            @RequestParam(required = false) Boolean active) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        List<Service> services;
        if (Boolean.TRUE.equals(active)) {
            services = serviceManagementService.getActiveServices(tenantId);
        } else {
            services = serviceManagementService.getServicesByTenant(tenantId);
        }
        List<ServiceResponseDTO> response = services.stream()
                .map(serviceMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Get a service by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> getService(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Service service = serviceManagementService.getServiceById(tenantId, id);
        return ResponseEntity.ok(serviceMapper.toResponseDTO(service));
    }

    /**
     * Update a service.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponseDTO> updateService(
            @PathVariable UUID id,
            @Valid @RequestBody ServiceRequestDTO request) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Service service = serviceManagementService.updateService(tenantId, id, request);
        return ResponseEntity.ok(serviceMapper.toResponseDTO(service));
    }

    /**
     * Delete (soft-delete) a service.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteService(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        serviceManagementService.deleteService(tenantId, id);
    }

    // ==================== Service Activation ====================

    /**
     * Activate a service.
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponseDTO> activateService(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Service service = serviceManagementService.activateService(tenantId, id);
        return ResponseEntity.ok(serviceMapper.toResponseDTO(service));
    }

    /**
     * Deactivate a service.
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponseDTO> deactivateService(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Service service = serviceManagementService.deactivateService(tenantId, id);
        return ResponseEntity.ok(serviceMapper.toResponseDTO(service));
    }

    // ==================== Staff Assignment ====================

    /**
     * Assign a staff member to a service.
     */
    @PostMapping("/{id}/staff/{staffId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void assignStaffToService(
            @PathVariable UUID id,
            @PathVariable UUID staffId) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        serviceManagementService.assignStaffToService(tenantId, id, staffId);
    }

    /**
     * Remove a staff member from a service.
     */
    @DeleteMapping("/{id}/staff/{staffId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void removeStaffFromService(
            @PathVariable UUID id,
            @PathVariable UUID staffId) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        serviceManagementService.removeStaffFromService(tenantId, id, staffId);
    }

    /**
     * Get all staff assigned to a service.
     */
    @GetMapping("/{id}/staff")
    public ResponseEntity<List<StaffResponseDTO>> getServiceStaff(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        List<Staff> staff = serviceManagementService.getServiceStaff(tenantId, id);
        List<StaffResponseDTO> response = staff.stream()
                .map(staffMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    // ==================== Categories (Sub-resource) ====================

    /**
     * Get all service categories.
     */
    @GetMapping("/categories")
    public ResponseEntity<List<ServiceCategoryResponseDTO>> getCategories() {
        UUID tenantId = TenantContext.getCurrentTenantId();
        List<ServiceCategory> categories = serviceCategoryRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId);
        List<ServiceCategoryResponseDTO> response = categories.stream()
                .map(serviceCategoryMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Create a new service category.
     */
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceCategoryResponseDTO> createCategory(
            @Valid @RequestBody ServiceCategoryRequestDTO request) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        ServiceCategory category = serviceCategoryMapper.toEntity(request);
        category.setTenantId(tenantId);
        ServiceCategory saved = serviceCategoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceCategoryMapper.toResponseDTO(saved));
    }
}
