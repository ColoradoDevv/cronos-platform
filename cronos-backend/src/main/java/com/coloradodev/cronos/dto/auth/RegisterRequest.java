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

    @jakarta.validation.constraints.NotBlank(message = "First name is required")
    private String firstName;

    @jakarta.validation.constraints.NotBlank(message = "Last name is required")
    private String lastName;

    @jakarta.validation.constraints.NotBlank(message = "Email is required")
    @jakarta.validation.constraints.Email(message = "Invalid email format")
    private String email;

    @jakarta.validation.constraints.NotBlank(message = "Password is required")
    @jakarta.validation.constraints.Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    private String tenantId; // Optional, if registering for existing tenant
    // Or we might want to create a new tenant on registration?
    // For now, let's assume we register a user into an existing tenant or create
    // one.
    // Let's keep it simple: register user.
}
