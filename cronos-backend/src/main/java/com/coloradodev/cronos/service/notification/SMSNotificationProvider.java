package com.coloradodev.cronos.service.notification;

/**
 * Provider interface for SMS notifications (placeholder for future
 * implementation).
 */
public interface SMSNotificationProvider {

    /**
     * Send an SMS message.
     */
    boolean sendSMS(String phoneNumber, String message);

    /**
     * Check if SMS is available for the given phone number.
     */
    boolean isAvailable(String phoneNumber);
}
