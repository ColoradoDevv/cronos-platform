package com.coloradodev.cronos.task;

import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.repository.BookingRepository;
import com.coloradodev.cronos.service.notification.EmailNotificationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Scheduled background tasks for the Cronos platform.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {

    private final BookingRepository bookingRepository;
    private final EmailNotificationProvider emailProvider;

    /**
     * Send appointment reminders daily at 8 AM.
     * Sends reminders for appointments scheduled for the next day.
     */
    @Scheduled(cron = "0 0 8 * * *", zone = "America/New_York")
    public void sendDailyReminders() {
        log.info("Starting daily appointment reminders job");

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime startOfDay = tomorrow.atStartOfDay();
        LocalDateTime endOfDay = tomorrow.atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByStartTimeBetweenAndStatusIn(
                startOfDay, endOfDay,
                List.of(Booking.BookingStatus.CONFIRMED));

        int sent = 0;
        for (Booking booking : bookings) {
            try {
                emailProvider.sendAppointmentReminder(booking, 24);
                sent++;
            } catch (Exception e) {
                log.error("Failed to send reminder for booking {}: {}",
                        booking.getId(), e.getMessage());
            }
        }

        log.info("Daily reminders completed: {}/{} emails sent", sent, bookings.size());
    }

    /**
     * Clean up old cancelled bookings at 2 AM.
     * Removes cancelled bookings older than 30 days.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldBookings() {
        log.info("Starting cleanup of old cancelled bookings");

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);

        // Get cancelled bookings older than cutoff
        List<Booking> oldCancelled = bookingRepository.findByStatusAndStartTimeBefore(
                Booking.BookingStatus.CANCELLED, cutoffDate);

        for (Booking booking : oldCancelled) {
            bookingRepository.delete(booking);
        }

        log.info("Cleanup completed: {} old bookings deleted", oldCancelled.size());
    }

    /**
     * Mark no-shows at 1 AM (for yesterday's appointments).
     * Appointments that were confirmed but not completed are marked as no-shows.
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void markNoShows() {
        log.info("Starting no-show detection");

        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfYesterday = yesterday.atStartOfDay();
        LocalDateTime endOfYesterday = yesterday.atTime(LocalTime.MAX);

        List<Booking> pendingBookings = bookingRepository.findByStartTimeBetweenAndStatusIn(
                startOfYesterday, endOfYesterday,
                List.of(Booking.BookingStatus.CONFIRMED));

        int marked = 0;
        for (Booking booking : pendingBookings) {
            booking.setStatus(Booking.BookingStatus.NO_SHOW);
            bookingRepository.save(booking);
            marked++;
        }

        log.info("No-show detection completed: {} appointments marked", marked);
    }

    /**
     * Check subscription expirations at 3 AM.
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void checkSubscriptionExpirations() {
        log.info("Checking subscription expirations");
        // TODO: Implement subscription expiration logic
        // - Find subscriptions expiring in 7 days → send warning email
        // - Find expired subscriptions → update status, notify tenant
        log.info("Subscription check completed");
    }
}
