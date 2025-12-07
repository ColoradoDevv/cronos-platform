package com.coloradodev.cronos.dto.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Public-facing service DTO (for client booking pages).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicePublicDTO {

    private UUID id;
    private String name;
    private String description;
    private Integer duration;
    private BigDecimal price;
    private String categoryName;
}
