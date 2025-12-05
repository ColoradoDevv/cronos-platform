package com.coloradodev.cronos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantPublicDto {
    private UUID id;
    private String name;
    private String slug;
    private String primaryColor;
    private String logoUrl;
    private LocalTime workDayStart;
    private LocalTime workDayEnd;
}
