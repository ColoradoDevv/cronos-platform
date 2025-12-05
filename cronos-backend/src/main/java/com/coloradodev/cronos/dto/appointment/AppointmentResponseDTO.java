package com.coloradodev.cronos.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDTO {
    private UUID id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private UUID serviceId;
    private String serviceName;
    private UUID userId;
    private String userName;
}
