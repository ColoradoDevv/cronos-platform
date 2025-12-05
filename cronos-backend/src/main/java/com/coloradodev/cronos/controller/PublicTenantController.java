package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.dto.TenantPublicDto;
import com.coloradodev.cronos.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/tenants")
@RequiredArgsConstructor
public class PublicTenantController {

    private final TenantService tenantService;

    @GetMapping("/{slug}")
    public ResponseEntity<TenantPublicDto> getTenantBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(tenantService.getPublicTenantBySlug(slug));
    }
}
