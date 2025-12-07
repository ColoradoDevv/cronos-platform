package com.coloradodev.cronos.dto.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Lightweight summary DTO for Service (for use in nested references).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceSummaryDTO {

    private UUID id;
    private String name;
    private Integer duration;
    private BigDecimal price;
}
