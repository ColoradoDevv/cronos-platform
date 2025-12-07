package com.coloradodev.cronos.dto.payment;

import com.coloradodev.cronos.domain.Payment.PaymentMethod;
import com.coloradodev.cronos.validation.ValidPrice;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for creating a Payment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    private UUID bookingId;

    private UUID appointmentId;

    @NotNull(message = "Amount is required")
    @ValidPrice
    private BigDecimal amount;

    @Size(max = 3, min = 3, message = "Currency must be 3 characters")
    private String currency;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    private String transactionId;
}
