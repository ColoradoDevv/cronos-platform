package com.coloradodev.cronos.dto.staff;

import com.coloradodev.cronos.dto.service.ServiceSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Full response DTO for Staff entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponseDTO {

    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String position;
    private String bio;
    private String photoUrl;
    private Boolean isActive;
    private Set<ServiceSummaryDTO> services;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
