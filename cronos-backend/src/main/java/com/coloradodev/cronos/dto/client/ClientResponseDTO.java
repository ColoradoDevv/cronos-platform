package com.coloradodev.cronos.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for Client entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String notes;
    private ClientPreferencesDTO preferences;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
