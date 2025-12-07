package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.core.tenant.TenantContext;
import com.coloradodev.cronos.domain.BusinessHours;
import com.coloradodev.cronos.dto.businesshours.BusinessHoursRequestDTO;
import com.coloradodev.cronos.dto.businesshours.BusinessHoursResponseDTO;
import com.coloradodev.cronos.dto.mapper.BusinessHoursMapper;
import com.coloradodev.cronos.service.BusinessHoursService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing business hours.
 */
@RestController
@RequestMapping("/api/business-hours")
@RequiredArgsConstructor
public class BusinessHoursController {

    private final BusinessHoursService businessHoursService;
    private final BusinessHoursMapper businessHoursMapper;

    /**
     * Get all business hours for the current tenant.
     */
    @GetMapping
    public ResponseEntity<List<BusinessHoursResponseDTO>> getBusinessHours() {
        UUID tenantId = TenantContext.getCurrentTenantId();
        List<BusinessHours> hours = businessHoursService.getBusinessHours(tenantId);
        List<BusinessHoursResponseDTO> response = hours.stream()
                .map(businessHoursMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Create or update business hours.
     */
    @PostMapping
    public ResponseEntity<BusinessHoursResponseDTO> setBusinessHours(
            @Valid @RequestBody BusinessHoursRequestDTO request) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        Boolean isOpen = request.getIsOpen() != null ? request.getIsOpen()
                : (request.getIsClosed() != null ? !request.getIsClosed() : true);

        BusinessHours hours = businessHoursService.setBusinessHours(
                tenantId,
                request.getDayOfWeek(),
                request.getOpenTime(),
                request.getCloseTime(),
                isOpen);

        return ResponseEntity.status(HttpStatus.CREATED).body(businessHoursMapper.toResponseDTO(hours));
    }

    /**
     * Update business hours for a specific day.
     */
    @PutMapping("/{dayOfWeek}")
    public ResponseEntity<BusinessHoursResponseDTO> updateBusinessHours(
            @PathVariable DayOfWeek dayOfWeek,
            @Valid @RequestBody BusinessHoursRequestDTO request) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        Boolean isOpen = request.getIsOpen() != null ? request.getIsOpen()
                : (request.getIsClosed() != null ? !request.getIsClosed() : true);

        BusinessHours hours = businessHoursService.setBusinessHours(
                tenantId,
                dayOfWeek,
                request.getOpenTime(),
                request.getCloseTime(),
                isOpen);

        return ResponseEntity.ok(businessHoursMapper.toResponseDTO(hours));
    }

    /**
     * Get open days for the current tenant.
     */
    @GetMapping("/open-days")
    public ResponseEntity<List<BusinessHoursResponseDTO>> getOpenDays() {
        UUID tenantId = TenantContext.getCurrentTenantId();
        List<BusinessHours> hours = businessHoursService.getOpenDays(tenantId);
        List<BusinessHoursResponseDTO> response = hours.stream()
                .map(businessHoursMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Initialize default business hours for the tenant.
     */
    @PostMapping("/initialize-defaults")
    @ResponseStatus(HttpStatus.CREATED)
    public void initializeDefaults() {
        UUID tenantId = TenantContext.getCurrentTenantId();
        businessHoursService.initializeDefaultHours(tenantId);
    }
}
