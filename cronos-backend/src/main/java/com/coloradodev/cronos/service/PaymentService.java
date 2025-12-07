package com.coloradodev.cronos.service;

import com.coloradodev.cronos.domain.Payment;
import com.coloradodev.cronos.domain.Payment.PaymentStatus;
import com.coloradodev.cronos.exception.BusinessRuleException;
import com.coloradodev.cronos.exception.ResourceNotFoundException;
import com.coloradodev.cronos.repository.PaymentRepository;
import com.coloradodev.cronos.service.payment.PaymentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for payment processing and management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProvider paymentProvider;
    private final AuditService auditService;

    /**
     * Create a payment record for a booking.
     */
    @Transactional
    public Payment createPayment(UUID tenantId, UUID bookingId, BigDecimal amount, String method) {
        Payment payment = new Payment();
        payment.setTenantId(tenantId);
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setMethod(Payment.PaymentMethod.valueOf(method.toUpperCase()));
        payment.setStatus(PaymentStatus.PENDING);

        Payment saved = paymentRepository.save(payment);

        auditService.logCreate(tenantId, null, "Payment", saved.getId(),
                Map.of("amount", amount, "method", method, "bookingId", bookingId));

        log.info("Created payment {} for booking {} amount ${}", saved.getId(), bookingId, amount);
        return saved;
    }

    /**
     * Process a pending payment.
     */
    @Transactional
    public Payment processPayment(UUID tenantId, UUID paymentId) {
        Payment payment = getPaymentById(tenantId, paymentId);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessRuleException("INVALID_STATUS",
                    "Only pending payments can be processed. Current status: " + payment.getStatus());
        }

        // Process via provider
        String transactionId = paymentProvider.processPayment(payment, payment.getAmount());

        if (transactionId != null) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(transactionId);
            payment.setPaidAt(LocalDateTime.now());

            auditService.logAction(tenantId, null, "PROCESS", "Payment", paymentId,
                    Map.of("status", "PENDING"),
                    Map.of("status", "COMPLETED", "transactionId", transactionId));

            log.info("Payment {} processed successfully. Transaction: {}", paymentId, transactionId);
        } else {
            payment.setStatus(PaymentStatus.FAILED);

            auditService.logAction(tenantId, null, "PROCESS_FAILED", "Payment", paymentId,
                    Map.of("status", "PENDING"),
                    Map.of("status", "FAILED"));

            log.warn("Payment {} processing failed", paymentId);
        }

        return paymentRepository.save(payment);
    }

    /**
     * Refund a completed payment.
     */
    @Transactional
    public Payment refundPayment(UUID tenantId, UUID paymentId, BigDecimal amount, String reason) {
        Payment payment = getPaymentById(tenantId, paymentId);

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BusinessRuleException("INVALID_STATUS",
                    "Only completed payments can be refunded. Current status: " + payment.getStatus());
        }

        if (amount.compareTo(payment.getAmount()) > 0) {
            throw new BusinessRuleException("AMOUNT_EXCEEDS",
                    "Refund amount cannot exceed payment amount");
        }

        // Process refund via provider
        boolean refunded = paymentProvider.refund(payment.getTransactionId(), amount, reason);

        if (refunded) {
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setRefundAmount(amount);
            payment.setRefundReason(reason);
            payment.setRefundedAt(LocalDateTime.now());

            auditService.logAction(tenantId, null, "REFUND", "Payment", paymentId,
                    Map.of("status", "COMPLETED"),
                    Map.of("status", "REFUNDED", "refundAmount", amount, "reason", reason));

            log.info("Payment {} refunded ${} - {}", paymentId, amount, reason);
        } else {
            throw new BusinessRuleException("REFUND_FAILED", "Refund processing failed");
        }

        return paymentRepository.save(payment);
    }

    /**
     * Get payment by ID.
     */
    @Transactional(readOnly = true)
    public Payment getPaymentById(UUID tenantId, UUID paymentId) {
        return paymentRepository.findByTenantIdAndId(tenantId, paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId.toString()));
    }

    /**
     * Get payments for a booking.
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByBooking(UUID tenantId, UUID bookingId) {
        return paymentRepository.findByTenantIdAndBookingId(tenantId, bookingId);
    }

    /**
     * Get payments for a tenant with pagination.
     */
    @Transactional(readOnly = true)
    public Page<Payment> getPaymentsByTenant(UUID tenantId, Pageable pageable) {
        List<Payment> payments = paymentRepository.findByTenantId(tenantId);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), payments.size());

        if (start > payments.size()) {
            return new PageImpl<>(List.of(), pageable, payments.size());
        }

        return new PageImpl<>(payments.subList(start, end), pageable, payments.size());
    }

    /**
     * Get payments by status.
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByStatus(UUID tenantId, PaymentStatus status) {
        return paymentRepository.findByTenantIdAndStatus(tenantId, status);
    }

    /**
     * Calculate total revenue for a date range.
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateRevenue(UUID tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Payment> payments = paymentRepository.findByTenantIdAndPaidAtBetween(
                tenantId, startDate, endDate);

        return payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
