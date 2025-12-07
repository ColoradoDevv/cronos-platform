package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.core.tenant.TenantContext;
import com.coloradodev.cronos.domain.Tenant;
import com.coloradodev.cronos.dto.mapper.TenantMapper;
import com.coloradodev.cronos.dto.tenant.TenantBrandingDTO;
import com.coloradodev.cronos.dto.tenant.TenantOnboardingRequest;
import com.coloradodev.cronos.dto.tenant.TenantRequestDTO;
import com.coloradodev.cronos.dto.tenant.TenantResponseDTO;
import com.coloradodev.cronos.dto.tenant.TenantSettingsDTO;
import com.coloradodev.cronos.exception.ResourceNotFoundException;
import com.coloradodev.cronos.repository.TenantRepository;
import com.coloradodev.cronos.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for tenant management.
 */
@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;
    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    /**
     * Create a new tenant (onboarding).
     */
    @PostMapping
    public ResponseEntity<?> createTenant(@Valid @RequestBody TenantOnboardingRequest request) {
        return ResponseEntity.ok(tenantService.createTenant(request));
    }

    /**
     * Get the current tenant's details.
     */
    @GetMapping("/current")
    public ResponseEntity<TenantResponseDTO> getCurrentTenant() {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId.toString()));
        return ResponseEntity.ok(tenantMapper.toResponseDTO(tenant));
    }

    /**
     * Update the current tenant.
     */
    @PutMapping("/current")
    public ResponseEntity<TenantResponseDTO> updateCurrentTenant(
            @Valid @RequestBody TenantRequestDTO request) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId.toString()));

        tenantMapper.updateEntityFromDTO(request, tenant);
        Tenant saved = tenantRepository.save(tenant);

        return ResponseEntity.ok(tenantMapper.toResponseDTO(saved));
    }

    /**
     * Update tenant branding.
     */
    @PatchMapping("/current/branding")
    public ResponseEntity<TenantResponseDTO> updateBranding(
            @Valid @RequestBody TenantBrandingDTO branding) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId.toString()));

        if (branding.getPrimaryColor() != null) {
            tenant.setPrimaryColor(branding.getPrimaryColor());
        }
        if (branding.getLogoUrl() != null) {
            tenant.setLogoUrl(branding.getLogoUrl());
        }

        Tenant saved = tenantRepository.save(tenant);

        return ResponseEntity.ok(tenantMapper.toResponseDTO(saved));
    }

    /**
     * Get tenant settings.
     */
    @GetMapping("/current/settings")
    public ResponseEntity<TenantSettingsDTO> getSettings() {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId.toString()));

        TenantSettingsDTO settings = TenantSettingsDTO.builder()
                .workDayStart(tenant.getWorkDayStart())
                .workDayEnd(tenant.getWorkDayEnd())
                .build();

        return ResponseEntity.ok(settings);
    }

    /**
     * Update tenant settings.
     */
    @PutMapping("/current/settings")
    public ResponseEntity<TenantSettingsDTO> updateSettings(
            @Valid @RequestBody TenantSettingsDTO settings) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId.toString()));

        if (settings.getWorkDayStart() != null) {
            tenant.setWorkDayStart(settings.getWorkDayStart());
        }
        if (settings.getWorkDayEnd() != null) {
            tenant.setWorkDayEnd(settings.getWorkDayEnd());
        }

        tenantRepository.save(tenant);

        return ResponseEntity.ok(settings);
    }
}
