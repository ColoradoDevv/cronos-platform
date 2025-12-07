package com.coloradodev.cronos.dto.staff;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Request DTO for creating/updating Staff.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffRequestDTO {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @Size(max = 100, message = "Position must not exceed 100 characters")
    private String position;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    private String photoUrl;
    
    private Boolean isActive;
    
    private Set<UUID> serviceIds;
}
