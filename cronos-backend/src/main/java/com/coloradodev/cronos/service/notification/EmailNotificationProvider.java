package com.coloradodev.cronos.service.notification;

import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.domain.Staff;

/**
 * Provider interface for email notifications.
 */
public interface EmailNotificationProvider {

    /**
     * Send a booking confirmation email.
     */
    boolean sendBookingConfirmation(Booking booking);

    /**
     * Send a booking cancellation email.
     */
    boolean sendBookingCancellation(Booking booking, String reason);

    /**
     * Send a booking reschedule notification.
     */
    boolean sendBookingReschedule(Booking booking, String oldTime, String newTime);

    /**
     * Send an appointment reminder email.
     */
    boolean sendAppointmentReminder(Booking booking, int hoursBeforeAppointment);

    /**
     * Send a notification to a staff member.
     */
    boolean sendStaffNotification(Staff staff, String subject, String message);

    /**
     * Send a generic email.
     */
    boolean sendEmail(String to, String subject, String htmlBody);
}
