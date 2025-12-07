package com.coloradodev.cronos.dto.booking;

import com.coloradodev.cronos.validation.PhoneNumber;
import com.coloradodev.cronos.validation.ValidTimeRange;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request DTO for creating a Booking.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidTimeRange
public class BookingRequestDTO {

    @NotNull(message = "Service ID is required")
    private UUID serviceId;

    private UUID staffId; // Optional specific staff

    private UUID clientId; // For registered clients

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    // Guest booking fields
    @Size(max = 200, message = "Client name must not exceed 200 characters")
    private String clientName;

    @Email(message = "Email must be valid")
    private String clientEmail;

    @PhoneNumber
    private String clientPhone;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
