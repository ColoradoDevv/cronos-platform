package com.coloradodev.cronos.service;

import com.coloradodev.cronos.domain.Service;
import com.coloradodev.cronos.domain.Staff;
import com.coloradodev.cronos.dto.service.ServiceRequestDTO;
import com.coloradodev.cronos.exception.ResourceNotFoundException;
import com.coloradodev.cronos.repository.ServiceRepository;
import com.coloradodev.cronos.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing tenant services (the offerings, not Spring services).
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class ServiceManagementService {

    private final ServiceRepository serviceRepository;
    private final StaffRepository staffRepository;
    private final AuditService auditService;
    private final SubscriptionService subscriptionService;

    /**
     * Create a new service for a tenant.
     */
    @Transactional
    public Service createService(UUID tenantId, ServiceRequestDTO request) {
        // Check subscription limits
        subscriptionService.validateLimit(tenantId, SubscriptionService.LimitType.SERVICES);

        Service service = Service.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .description(request.getDescription())
                .duration(request.getDuration())
                .price(request.getPrice())
                .categoryId(request.getCategoryId())
                .isActive(true)
                .build();

        Service saved = serviceRepository.save(service);

        auditService.logCreate(tenantId, null, "Service", saved.getId(),
                Map.of("name", saved.getName(), "price", saved.getPrice()));

        log.info("Created service {} for tenant {}", saved.getName(), tenantId);
        return saved;
    }

    /**
     * Update an existing service.
     */
    @Transactional
    public Service updateService(UUID tenantId, UUID serviceId, ServiceRequestDTO request) {
        Service service = getServiceById(tenantId, serviceId);

        Map<String, Object> oldValues = Map.of(
                "name", service.getName(),
                "description", service.getDescription() != null ? service.getDescription() : "",
                "duration", service.getDuration(),
                "price", service.getPrice());

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setDuration(request.getDuration());
        service.setPrice(request.getPrice());
        if (request.getCategoryId() != null) {
            service.setCategoryId(request.getCategoryId());
        }

        Service saved = serviceRepository.save(service);

        Map<String, Object> newValues = Map.of(
                "name", saved.getName(),
                "description", saved.getDescription() != null ? saved.getDescription() : "",
                "duration", saved.getDuration(),
                "price", saved.getPrice());

        auditService.logUpdate(tenantId, null, "Service", saved.getId(), oldValues, newValues);

        log.info("Updated service {} for tenant {}", saved.getName(), tenantId);
        return saved;
    }

    /**
     * Soft delete a service.
     */
    @Transactional
    public void deleteService(UUID tenantId, UUID serviceId) {
        Service service = getServiceById(tenantId, serviceId);

        service.setIsActive(false);
        serviceRepository.save(service);

        auditService.logDelete(tenantId, null, "Service", serviceId,
                Map.of("name", service.getName()));

        log.info("Soft deleted service {} for tenant {}", service.getName(), tenantId);
    }

    /**
     * Get a service by ID.
     */
    @Transactional(readOnly = true)
    public Service getServiceById(UUID tenantId, UUID serviceId) {
        return serviceRepository.findByTenantIdAndId(tenantId, serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service", serviceId.toString()));
    }

    /**
     * Get all services for a tenant.
     */
    @Transactional(readOnly = true)
    public List<Service> getServicesByTenant(UUID tenantId) {
        return serviceRepository.findByTenantId(tenantId);
    }

    /**
     * Get active services for a tenant.
     */
    @Transactional(readOnly = true)
    public List<Service> getActiveServices(UUID tenantId) {
        return serviceRepository.findByTenantIdAndIsActiveOrderByNameAsc(tenantId, true);
    }

    /**
     * Get services by category.
     */
    @Transactional(readOnly = true)
    public List<Service> getServicesByCategory(UUID tenantId, UUID categoryId) {
        return serviceRepository.findByTenantIdAndCategoryId(tenantId, categoryId);
    }

    /**
     * Assign a staff member to a service.
     */
    @Transactional
    public void assignStaffToService(UUID tenantId, UUID serviceId, UUID staffId) {
        Service service = getServiceById(tenantId, serviceId);
        Staff staff = staffRepository.findByTenantIdAndId(tenantId, staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", staffId.toString()));

        staff.getServices().add(service);
        staffRepository.save(staff);

        auditService.logAction(tenantId, null, "ASSIGN_STAFF", "Service", serviceId,
                null, Map.of("staffId", staffId));

        log.info("Assigned staff {} to service {} for tenant {}", staffId, serviceId, tenantId);
    }

    /**
     * Remove a staff member from a service.
     */
    @Transactional
    public void removeStaffFromService(UUID tenantId, UUID serviceId, UUID staffId) {
        Service service = getServiceById(tenantId, serviceId);
        Staff staff = staffRepository.findByTenantIdAndId(tenantId, staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", staffId.toString()));

        staff.getServices().remove(service);
        staffRepository.save(staff);

        auditService.logAction(tenantId, null, "REMOVE_STAFF", "Service", serviceId,
                Map.of("staffId", staffId), null);

        log.info("Removed staff {} from service {} for tenant {}", staffId, serviceId, tenantId);
    }

    /**
     * Update service pricing.
     */
    @Transactional
    public Service updateServicePricing(UUID tenantId, UUID serviceId, BigDecimal newPrice) {
        Service service = getServiceById(tenantId, serviceId);

        BigDecimal oldPrice = service.getPrice();
        service.setPrice(newPrice);

        Service saved = serviceRepository.save(service);

        auditService.logUpdate(tenantId, null, "Service", serviceId,
                Map.of("price", oldPrice),
                Map.of("price", newPrice));

        log.info("Updated pricing for service {} from {} to {}", serviceId, oldPrice, newPrice);
        return saved;
    }

    /**
     * Activate a service.
     */
    @Transactional
    public Service activateService(UUID tenantId, UUID serviceId) {
        Service service = getServiceById(tenantId, serviceId);

        service.setIsActive(true);
        Service saved = serviceRepository.save(service);

        auditService.logAction(tenantId, null, "ACTIVATE", "Service", serviceId);

        log.info("Activated service {} for tenant {}", serviceId, tenantId);
        return saved;
    }

    /**
     * Deactivate a service.
     */
    @Transactional
    public Service deactivateService(UUID tenantId, UUID serviceId) {
        Service service = getServiceById(tenantId, serviceId);

        service.setIsActive(false);
        Service saved = serviceRepository.save(service);

        auditService.logAction(tenantId, null, "DEACTIVATE", "Service", serviceId);

        log.info("Deactivated service {} for tenant {}", serviceId, tenantId);
        return saved;
    }

    /**
     * Get all staff members assigned to a service.
     */
    @Transactional(readOnly = true)
    public List<Staff> getServiceStaff(UUID tenantId, UUID serviceId) {
        // Verify service exists
        getServiceById(tenantId, serviceId);
        return staffRepository.findByTenantIdAndServiceId(tenantId, serviceId);
    }
}
