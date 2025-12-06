package com.coloradodev.cronos.core.security;

import com.coloradodev.cronos.core.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // SaaS Rule: Validate Tenant
                    String tokenTenantId = jwtService.extractClaim(jwt,
                            claims -> claims.get("tenant_id", String.class));
                    String headerTenantId = TenantContext.getCurrentTenant();

                    if (tokenTenantId != null && headerTenantId != null && !tokenTenantId.equals(headerTenantId)) {
                        log.warn("Tenant mismatch! Token: {}, Header: {}", tokenTenantId, headerTenantId);
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("Tenant mismatch");
                        return;
                    }

                    // If header is missing but token has tenant, maybe we should set context?
                    // For now, we rely on TenantInterceptor to have set the context from header.
                    // If TenantInterceptor failed (no header), it would have returned 400 already
                    // (if configured to do so).
                    // But this filter runs BEFORE TenantInterceptor? No, usually filters run in
                    // order.
                    // We need to ensure TenantInterceptor runs before or we check header here
                    // manually if needed.
                    // Actually, TenantInterceptor is a HandlerInterceptor (Spring MVC), which runs
                    // AFTER Servlet Filters.
                    // So TenantContext might NOT be set yet!
                    // We should extract header manually here to be safe or ensure order.
                    // Let's extract header manually here to be safe for the check.

                    String currentTenantId = request.getHeader("X-Tenant-ID");
                    if (tokenTenantId != null && currentTenantId != null && !tokenTenantId.equals(currentTenantId)) {
                        log.warn("Tenant mismatch! Token: {}, Header: {}", tokenTenantId, currentTenantId);
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("Tenant mismatch");
                        return;
                    }

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (UsernameNotFoundException e) {
                // User from token doesn't exist anymore - invalid token, just continue without
                // authentication
                log.warn("Token contains non-existent user: {}", userEmail);
            }
        }
        filterChain.doFilter(request, response);
    }
}
