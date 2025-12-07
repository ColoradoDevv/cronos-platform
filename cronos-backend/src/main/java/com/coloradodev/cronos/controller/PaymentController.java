package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.core.tenant.TenantContext;
import com.coloradodev.cronos.domain.Payment;
import com.coloradodev.cronos.dto.mapper.PaymentMapper;
import com.coloradodev.cronos.dto.payment.PaymentRequestDTO;
import com.coloradodev.cronos.dto.payment.PaymentResponseDTO;
import com.coloradodev.cronos.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for payment management.
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    /**
     * Create a new payment.
     */
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @Valid @RequestBody PaymentRequestDTO request) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Payment payment = paymentService.createPayment(
                tenantId,
                request.getBookingId(),
                request.getAmount(),
                request.getMethod().name());
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toResponseDTO(payment));
    }

    /**
     * Get all payments with pagination.
     */
    @GetMapping
    public ResponseEntity<Page<PaymentResponseDTO>> getPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Payment> payments = paymentService.getPaymentsByTenant(tenantId, pageable);
        Page<PaymentResponseDTO> response = payments.map(paymentMapper::toResponseDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a payment by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPayment(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Payment payment = paymentService.getPaymentById(tenantId, id);
        return ResponseEntity.ok(paymentMapper.toResponseDTO(payment));
    }

    /**
     * Refund a payment.
     */
    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponseDTO> refundPayment(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        BigDecimal amount = body.get("amount") != null
                ? new BigDecimal(body.get("amount").toString())
                : null;
        String reason = body.get("reason") != null
                ? body.get("reason").toString()
                : "Refund requested";

        // If amount not specified, get original payment amount
        if (amount == null) {
            Payment payment = paymentService.getPaymentById(tenantId, id);
            amount = payment.getAmount();
        }

        Payment refunded = paymentService.refundPayment(tenantId, id, amount, reason);
        return ResponseEntity.ok(paymentMapper.toResponseDTO(refunded));
    }

    /**
     * Get payments by booking ID.
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByBooking(@PathVariable UUID bookingId) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        List<Payment> payments = paymentService.getPaymentsByBooking(tenantId, bookingId);
        List<PaymentResponseDTO> response = payments.stream()
                .map(paymentMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }
}
