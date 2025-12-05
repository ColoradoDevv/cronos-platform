package com.coloradodev.cronos.dto.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantOnboardingRequest {

    private String name;
    private String slug;
    private String primaryColor;
    private String logoUrl;

    // Admin User details
    private String adminEmail;
    private String adminPassword;
    private String adminFirstName;
    private String adminLastName;
}
