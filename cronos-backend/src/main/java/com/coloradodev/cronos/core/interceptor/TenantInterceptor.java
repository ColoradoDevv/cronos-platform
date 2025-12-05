
package com.coloradodev.cronos.core.interceptor;

import com.coloradodev.cronos.core.tenant.TenantContext;
import com.coloradodev.cronos.domain.Tenant;
import com.coloradodev.cronos.repository.TenantRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private final TenantRepository tenantRepository;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler)
            throws Exception {
        String tenantId = request.getHeader(TENANT_HEADER);

        if (tenantId == null || tenantId.isEmpty()) {
            log.warn("Missing X-Tenant-ID header");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing X-Tenant-ID header");
            return false;
        }

        // Validate tenant exists (assuming tenantId is the UUID string)
        // In a real scenario, we might want to cache this lookup
        try {
            // Check if it's a UUID
            java.util.UUID uuid = java.util.UUID.fromString(tenantId);
            Optional<Tenant> tenant = tenantRepository.findById(uuid);

            if (tenant.isEmpty()) {
                log.warn("Tenant not found: {}", tenantId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid Tenant ID");
                return false;
            }

            TenantContext.setCurrentTenant(tenantId);
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format for tenant: {}", tenantId);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid Tenant ID format");
            return false;
        }
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler,
            ModelAndView modelAndView) throws Exception {
        TenantContext.clear();
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler, Exception ex)
            throws Exception {
        TenantContext.clear();
    }
}
