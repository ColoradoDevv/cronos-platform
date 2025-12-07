package com.coloradodev.cronos.dto.tenant;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating tenant branding.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantBrandingDTO {

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Primary color must be a valid hex color")
    private String primaryColor;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Secondary color must be a valid hex color")
    private String secondaryColor;

    private String logoUrl;

    private String faviconUrl;

    private String fontFamily;
}
