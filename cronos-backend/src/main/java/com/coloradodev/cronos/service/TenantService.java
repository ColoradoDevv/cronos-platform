package com.coloradodev.cronos.service;

import com.coloradodev.cronos.domain.Tenant;
import com.coloradodev.cronos.domain.User;
import com.coloradodev.cronos.dto.tenant.TenantOnboardingRequest;
import com.coloradodev.cronos.repository.TenantRepository;
import com.coloradodev.cronos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenantService {

        private final TenantRepository tenantRepository;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        @Transactional
        public Tenant createTenant(TenantOnboardingRequest request) {
                // 1. Create Tenant
                Tenant tenant = Tenant.builder()
                                .name(request.getName())
                                .slug(request.getSlug())
                                .primaryColor(request.getPrimaryColor())
                                .logoUrl(request.getLogoUrl())
                                .workDayStart(
                                                request.getWorkDayStart() != null ? request.getWorkDayStart()
                                                                : java.time.LocalTime.of(9, 0))
                                .workDayEnd(request.getWorkDayEnd() != null ? request.getWorkDayEnd()
                                                : java.time.LocalTime.of(17, 0))
                                .status("ACTIVE")
                                .build();

                tenant = tenantRepository.save(tenant);

                // 2. Create Admin User
                User adminUser = User.builder()
                                .firstName(request.getAdminFirstName())
                                .lastName(request.getAdminLastName())
                                .email(request.getAdminEmail())
                                .password(passwordEncoder.encode(request.getAdminPassword()))
                                .role("ADMIN")
                                .tenant(tenant)
                                .build();

                userRepository.save(adminUser);

                return tenant;
        }

        @Transactional(readOnly = true)
        public com.coloradodev.cronos.dto.TenantPublicDto getPublicTenantBySlug(String slug) {
                Tenant tenant = tenantRepository.findBySlug(slug)
                                .orElseThrow(() -> new RuntimeException("Tenant not found with slug: " + slug));

                return com.coloradodev.cronos.dto.TenantPublicDto.builder()
                                .id(tenant.getId())
                                .name(tenant.getName())
                                .slug(tenant.getSlug())
                                .primaryColor(tenant.getPrimaryColor())
                                .logoUrl(tenant.getLogoUrl())
                                .workDayStart(tenant.getWorkDayStart())
                                .workDayEnd(tenant.getWorkDayEnd())
                                .build();
        }
}
