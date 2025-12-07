package com.coloradodev.cronos.service.notification;

import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.domain.Staff;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Console implementation of EmailNotificationProvider for development/testing.
 * Logs emails to console instead of actually sending them.
 */
@Component
@Slf4j
public class ConsoleEmailProvider implements EmailNotificationProvider {

    @Override
    public boolean sendBookingConfirmation(Booking booking) {
        log.info("📧 BOOKING CONFIRMATION EMAIL");
        log.info("   To: {}", booking.getClientEmail());
        log.info("   Service: {} on {}",
                booking.getService() != null ? booking.getService().getName() : "Service",
                booking.getStartTime());
        log.info("   Status: {}", booking.getStatus());
        return true;
    }

    @Override
    public boolean sendBookingCancellation(Booking booking, String reason) {
        log.info("📧 BOOKING CANCELLATION EMAIL");
        log.info("   To: {}", booking.getClientEmail());
        log.info("   Reason: {}", reason);
        return true;
    }

    @Override
    public boolean sendBookingReschedule(Booking booking, String oldTime, String newTime) {
        log.info("📧 BOOKING RESCHEDULE EMAIL");
        log.info("   To: {}", booking.getClientEmail());
        log.info("   Changed: {} -> {}", oldTime, newTime);
        return true;
    }

    @Override
    public boolean sendAppointmentReminder(Booking booking, int hoursBeforeAppointment) {
        log.info("📧 APPOINTMENT REMINDER EMAIL");
        log.info("   To: {}", booking.getClientEmail());
        log.info("   Appointment in {} hours at {}", hoursBeforeAppointment, booking.getStartTime());
        return true;
    }

    @Override
    public boolean sendStaffNotification(Staff staff, String subject, String message) {
        log.info("📧 STAFF NOTIFICATION EMAIL");
        log.info("   To: Staff {}", staff.getId());
        log.info("   Subject: {}", subject);
        log.info("   Message: {}", message);
        return true;
    }

    @Override
    public boolean sendEmail(String to, String subject, String htmlBody) {
        log.info("📧 GENERIC EMAIL");
        log.info("   To: {}", to);
        log.info("   Subject: {}", subject);
        log.info("   Body: {}...", htmlBody.length() > 100 ? htmlBody.substring(0, 100) : htmlBody);
        return true;
    }
}
