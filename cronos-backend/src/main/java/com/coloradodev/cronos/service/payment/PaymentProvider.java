package com.coloradodev.cronos.service.payment;

import com.coloradodev.cronos.domain.Payment;

import java.math.BigDecimal;

/**
 * Provider interface for payment processing.
 */
public interface PaymentProvider {

    /**
     * Get the provider name.
     */
    String getProviderName();

    /**
     * Process a payment.
     * 
     * @return External transaction ID if successful, null if failed
     */
    String processPayment(Payment payment, BigDecimal amount);

    /**
     * Issue a refund.
     * 
     * @return true if refund was successful
     */
    boolean refund(String transactionId, BigDecimal amount, String reason);

    /**
     * Check if the provider is available.
     */
    boolean isAvailable();
}
