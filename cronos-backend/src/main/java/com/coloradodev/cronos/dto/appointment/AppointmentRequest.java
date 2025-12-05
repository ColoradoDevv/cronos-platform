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
public class AppointmentRequest {

    @jakarta.validation.constraints.NotNull(message = "Service ID is required")
    private UUID serviceId;

    @jakarta.validation.constraints.NotNull(message = "Start time is required")
    @jakarta.validation.constraints.Future(message = "Appointment must be in the future")
    private LocalDateTime startTime;
}
