package com.coloradodev.cronos.dto.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Full response DTO for Service entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseDTO {

    private UUID id;
    private String name;
    private String description;
    private Integer duration;
    private BigDecimal price;
    private UUID categoryId;
    private String categoryName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
