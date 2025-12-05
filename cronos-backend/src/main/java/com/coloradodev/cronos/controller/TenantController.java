package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.dto.tenant.TenantOnboardingRequest;
import com.coloradodev.cronos.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<?> createTenant(@jakarta.validation.Valid @RequestBody TenantOnboardingRequest request) {
        return ResponseEntity.ok(tenantService.createTenant(request));
    }
}
