package com.coloradodev.cronos.service.payment;

import com.coloradodev.cronos.domain.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mock payment provider for development/testing.
 * Simulates successful payments without actually charging.
 */
@Component
@Slf4j
public class MockPaymentProvider implements PaymentProvider {

    @Override
    public String getProviderName() {
        return "MOCK";
    }

    @Override
    public String processPayment(Payment payment, BigDecimal amount) {
        // Simulate payment processing
        log.info("💳 MOCK PAYMENT PROCESSED");
        log.info("   Amount: ${}", amount);
        log.info("   Booking: {}", payment.getBookingId());

        // Generate mock transaction ID
        String transactionId = "mock_txn_" + UUID.randomUUID().toString().substring(0, 8);
        log.info("   Transaction ID: {}", transactionId);

        return transactionId;
    }

    @Override
    public boolean refund(String transactionId, BigDecimal amount, String reason) {
        log.info("💳 MOCK REFUND PROCESSED");
        log.info("   Transaction: {}", transactionId);
        log.info("   Amount: ${}", amount);
        log.info("   Reason: {}", reason);
        return true;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
