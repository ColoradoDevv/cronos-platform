package com.coloradodev.cronos.service;

import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.domain.Notification;
import com.coloradodev.cronos.domain.Notification.NotificationStatus;
import com.coloradodev.cronos.domain.Staff;
import com.coloradodev.cronos.repository.NotificationRepository;
import com.coloradodev.cronos.service.notification.EmailNotificationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for sending and logging notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailNotificationProvider emailProvider;

    /**
     * Send booking confirmation notification.
     */
    @Transactional
    public Notification sendBookingConfirmation(Booking booking) {
        boolean sent = emailProvider.sendBookingConfirmation(booking);

        return logNotification(
                booking.getTenantId(),
                booking.getClientId(),
                "BOOKING_CONFIRMATION",
                "EMAIL",
                "Your booking has been received",
                buildBookingMessage(booking, "confirmed"),
                sent);
    }

    /**
     * Send appointment reminder notification.
     */
    @Transactional
    public Notification sendAppointmentReminder(Booking booking) {
        boolean sent = emailProvider.sendAppointmentReminder(booking, 24);

        return logNotification(
                booking.getTenantId(),
                booking.getClientId(),
                "APPOINTMENT_REMINDER",
                "EMAIL",
                "Reminder: Your appointment tomorrow",
                buildBookingMessage(booking, "reminder"),
                sent);
    }

    /**
     * Send cancellation notification.
     */
    @Transactional
    public Notification sendCancellationNotification(Booking booking, String reason) {
        boolean sent = emailProvider.sendBookingCancellation(booking, reason);

        return logNotification(
                booking.getTenantId(),
                booking.getClientId(),
                "BOOKING_CANCELLATION",
                "EMAIL",
                "Your booking has been cancelled",
                "Your booking has been cancelled. Reason: " + (reason != null ? reason : "Not specified"),
                sent);
    }

    /**
     * Send reschedule notification.
     */
    @Transactional
    public Notification sendRescheduleNotification(Booking booking, LocalDateTime oldTime) {
        boolean sent = emailProvider.sendBookingReschedule(
                booking,
                oldTime.toString(),
                booking.getStartTime().toString());

        return logNotification(
                booking.getTenantId(),
                booking.getClientId(),
                "BOOKING_RESCHEDULE",
                "EMAIL",
                "Your booking has been rescheduled",
                "Your booking has been rescheduled from " + oldTime + " to " + booking.getStartTime(),
                sent);
    }

    /**
     * Send notification to a staff member.
     */
    @Transactional
    public Notification sendStaffNotification(UUID tenantId, Staff staff, String subject, String message) {
        boolean sent = emailProvider.sendStaffNotification(staff, subject, message);

        return logNotification(
                tenantId,
                staff.getUserId(),
                "STAFF_NOTIFICATION",
                "EMAIL",
                subject,
                message,
                sent);
    }

    /**
     * Log a notification to the database.
     */
    @Transactional
    public Notification logNotification(UUID tenantId, UUID recipientId, String type,
            String channel, String subject, String content, boolean sent) {
        Notification notification = new Notification();
        notification.setTenantId(tenantId);
        notification.setRecipientId(recipientId);
        notification.setType(type);
        notification.setChannel(channel);
        notification.setSubject(subject);
        notification.setContent(content);
        notification.setStatus(sent ? NotificationStatus.SENT : NotificationStatus.FAILED);
        notification.setSentAt(sent ? LocalDateTime.now() : null);

        Notification saved = notificationRepository.save(notification);

        log.debug("Logged notification {} to {} via {}", type, recipientId, channel);
        return saved;
    }

    /**
     * Get pending notifications for retry.
     */
    @Transactional(readOnly = true)
    public java.util.List<Notification> getPendingNotifications() {
        return notificationRepository.findByStatus(NotificationStatus.PENDING);
    }

    /**
     * Mark notification as sent.
     */
    @Transactional
    public void markAsSent(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
        });
    }

    /**
     * Mark notification as failed.
     */
    @Transactional
    public void markAsFailed(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
        });
    }

    private String buildBookingMessage(Booking booking, String action) {
        return String.format(
                "Your booking has been %s.\nService: %s\nDate: %s\nTime: %s",
                action,
                booking.getService() != null ? booking.getService().getName() : "Service",
                booking.getStartTime().toLocalDate(),
                booking.getStartTime().toLocalTime());
    }
}
