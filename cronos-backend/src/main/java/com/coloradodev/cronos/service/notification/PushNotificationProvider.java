package com.coloradodev.cronos.service.notification;

/**
 * Provider interface for push notifications (placeholder for future
 * implementation).
 */
public interface PushNotificationProvider {

    /**
     * Send a push notification.
     */
    boolean sendPushNotification(String deviceToken, String title, String body);

    /**
     * Check if push notifications are available for the given device.
     */
    boolean isAvailable(String deviceToken);
}
