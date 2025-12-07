package com.coloradodev.cronos.dto.tenant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Request DTO for creating/updating a Tenant.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantRequestDTO {

    @NotBlank(message = "Tenant name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Slug is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
    @Size(max = 50, message = "Slug must not exceed 50 characters")
    private String slug;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Primary color must be a valid hex color")
    private String primaryColor;

    private String logoUrl;

    private LocalTime workDayStart;

    private LocalTime workDayEnd;
}
