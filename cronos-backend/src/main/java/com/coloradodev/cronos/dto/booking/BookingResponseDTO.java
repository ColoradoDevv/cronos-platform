package com.coloradodev.cronos.dto.booking;

import com.coloradodev.cronos.domain.Booking.BookingStatus;
import com.coloradodev.cronos.dto.service.ServiceSummaryDTO;
import com.coloradodev.cronos.dto.staff.StaffSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for Booking entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {

    private UUID id;
    private ServiceSummaryDTO service;
    private StaffSummaryDTO staff;
    private UUID clientId;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private String notes;
    private UUID appointmentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
