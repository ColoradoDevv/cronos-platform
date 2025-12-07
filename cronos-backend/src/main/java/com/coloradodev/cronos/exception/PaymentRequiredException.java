package com.coloradodev.cronos.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when payment is required to proceed.
 * HTTP 402 (Payment Required)
 */
public class PaymentRequiredException extends CronosException {

    public PaymentRequiredException(String message) {
        super(message, "PAYMENT_REQUIRED", HttpStatus.PAYMENT_REQUIRED);
    }

    public static PaymentRequiredException subscriptionExpired() {
        return new PaymentRequiredException("Your subscription has expired. Please renew to continue.");
    }

    public static PaymentRequiredException paymentFailed() {
        return new PaymentRequiredException("Payment processing failed. Please try again.");
    }

    public static PaymentRequiredException upgradeRequired() {
        return new PaymentRequiredException("This feature requires a plan upgrade.");
    }
}
