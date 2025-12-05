package com.coloradodev.cronos.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String tenantId; // Optional, if registering for existing tenant
    // Or we might want to create a new tenant on registration?
    // For now, let's assume we register a user into an existing tenant or create
    // one.
    // Let's keep it simple: register user.
}
