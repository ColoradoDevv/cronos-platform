package com.coloradodev.cronos.service.auth;

import com.coloradodev.cronos.core.security.JwtService;
import com.coloradodev.cronos.domain.Tenant;
import com.coloradodev.cronos.domain.User;
import com.coloradodev.cronos.dto.auth.AuthRequest;
import com.coloradodev.cronos.dto.auth.AuthResponse;
import com.coloradodev.cronos.dto.auth.RegisterRequest;
import com.coloradodev.cronos.repository.TenantRepository;
import com.coloradodev.cronos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        Tenant tenant = null;
        if (request.getTenantId() != null) {
            tenant = tenantRepository.findById(UUID.fromString(request.getTenantId()))
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER") // Default role
                .tenant(tenant)
                .build();

        userRepository.save(user);

        Map<String, Object> extraClaims = new HashMap<>();
        if (tenant != null) {
            extraClaims.put("tenant_id", tenant.getId().toString());
        }

        var jwtToken = jwtService.generateToken(extraClaims, user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        Map<String, Object> extraClaims = new HashMap<>();
        if (user.getTenant() != null) {
            extraClaims.put("tenant_id", user.getTenant().getId().toString());
        }

        var jwtToken = jwtService.generateToken(extraClaims, user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
