package com.coloradodev.cronos.service;

import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.domain.Booking.BookingStatus;
import com.coloradodev.cronos.domain.Payment;
import com.coloradodev.cronos.domain.Payment.PaymentStatus;
import com.coloradodev.cronos.repository.BookingRepository;
import com.coloradodev.cronos.repository.ClientRepository;
import com.coloradodev.cronos.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for generating business reports and analytics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingService {

        private final BookingRepository bookingRepository;
        private final PaymentRepository paymentRepository;
        private final ClientRepository clientRepository;

        /**
         * Get appointment statistics for a date range.
         */
        @Transactional(readOnly = true)
        public AppointmentStats getAppointmentStats(UUID tenantId, LocalDate startDate, LocalDate endDate) {
                LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
                LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

                List<Booking> bookings = bookingRepository.findByTenantIdAndDateRange(tenantId, start, end);

                // Count by status
                Map<BookingStatus, Long> byStatus = bookings.stream()
                                .collect(Collectors.groupingBy(Booking::getStatus, Collectors.counting()));

                // Count by service
                Map<UUID, Long> byService = bookings.stream()
                                .collect(Collectors.groupingBy(Booking::getServiceId, Collectors.counting()));

                // Count by staff
                Map<UUID, Long> byStaff = bookings.stream()
                                .filter(b -> b.getStaffId() != null)
                                .collect(Collectors.groupingBy(Booking::getStaffId, Collectors.counting()));

                return new AppointmentStats(
                                bookings.size(),
                                byStatus.getOrDefault(BookingStatus.CONFIRMED, 0L),
                                byStatus.getOrDefault(BookingStatus.CANCELLED, 0L),
                                byStatus.getOrDefault(BookingStatus.NO_SHOW, 0L),
                                byStatus.getOrDefault(BookingStatus.PENDING, 0L),
                                byService,
                                byStaff);
        }

        /**
         * Get revenue report for a date range.
         */
        @Transactional(readOnly = true)
        public RevenueReport getRevenueReport(UUID tenantId, LocalDate startDate, LocalDate endDate) {
                LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
                LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

                List<Payment> payments = paymentRepository.findByTenantIdAndPaidAtBetween(tenantId, start, end)
                                .stream()
                                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                                .toList();

                BigDecimal totalRevenue = payments.stream()
                                .map(Payment::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Revenue by payment method
                Map<String, BigDecimal> byMethod = payments.stream()
                                .collect(Collectors.toMap(
                                                p -> p.getMethod() != null ? p.getMethod().name() : "UNKNOWN",
                                                Payment::getAmount,
                                                BigDecimal::add));

                // Revenue by booking (to link to services)
                Map<UUID, BigDecimal> byBooking = payments.stream()
                                .filter(p -> p.getBookingId() != null)
                                .collect(Collectors.groupingBy(
                                                Payment::getBookingId,
                                                Collectors.reducing(BigDecimal.ZERO, Payment::getAmount,
                                                                BigDecimal::add)));

                // Refunds
                BigDecimal totalRefunds = paymentRepository.findByTenantIdAndStatus(tenantId, PaymentStatus.REFUNDED)
                                .stream()
                                .filter(p -> p.getRefundedAt() != null &&
                                                !p.getRefundedAt().isBefore(start) &&
                                                !p.getRefundedAt().isAfter(end))
                                .map(p -> p.getRefundAmount() != null ? p.getRefundAmount() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                return new RevenueReport(
                                totalRevenue,
                                totalRefunds,
                                totalRevenue.subtract(totalRefunds),
                                payments.size(),
                                byMethod,
                                byBooking);
        }

        /**
         * Get client statistics.
         */
        @Transactional(readOnly = true)
        public ClientStats getClientStats(UUID tenantId) {
                var clients = clientRepository.findByTenantId(tenantId);
                int totalClients = clients.size();

                // New clients this month
                LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                long newClientsThisMonth = clients.stream()
                                .filter(c -> c.getCreatedAt() != null && !c.getCreatedAt().isBefore(startOfMonth))
                                .count();

                // Clients with bookings (returning clients indicator)
                Map<UUID, Long> bookingCounts = new HashMap<>();
                for (var client : clients) {
                        long count = bookingRepository
                                        .findByTenantIdAndClientIdOrderByCreatedAtDesc(tenantId, client.getId())
                                        .size();
                        if (count > 0) {
                                bookingCounts.put(client.getId(), count);
                        }
                }

                long clientsWithBookings = bookingCounts.size();
                long returningClients = bookingCounts.values().stream()
                                .filter(count -> count > 1)
                                .count();

                // Top clients by booking count
                List<Map.Entry<UUID, Long>> topClients = bookingCounts.entrySet().stream()
                                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                                .limit(10)
                                .toList();

                return new ClientStats(
                                totalClients,
                                newClientsThisMonth,
                                clientsWithBookings,
                                returningClients,
                                topClients.stream()
                                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        /**
         * Get staff performance metrics.
         */
        @Transactional(readOnly = true)
        public StaffPerformance getStaffPerformance(UUID tenantId, UUID staffId, LocalDate startDate,
                        LocalDate endDate) {
                LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
                LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

                List<Booking> bookings = bookingRepository.findByTenantIdAndDateRange(tenantId, start, end)
                                .stream()
                                .filter(b -> staffId == null || staffId.equals(b.getStaffId()))
                                .toList();

                long completed = bookings.stream()
                                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                                .count();

                long cancelled = bookings.stream()
                                .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                                .count();

                long noShows = bookings.stream()
                                .filter(b -> b.getStatus() == BookingStatus.NO_SHOW)
                                .count();

                // Calculate revenue (would need to join with payments in real implementation)
                BigDecimal revenue = BigDecimal.ZERO; // Placeholder

                return new StaffPerformance(
                                staffId,
                                bookings.size(),
                                completed,
                                cancelled,
                                noShows,
                                revenue);
        }

        /**
         * Get no-show report.
         */
        @Transactional(readOnly = true)
        public NoShowReport getNoShowReport(UUID tenantId, LocalDate startDate, LocalDate endDate) {
                LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
                LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

                List<Booking> noShows = bookingRepository.findByTenantIdAndDateRange(tenantId, start, end)
                                .stream()
                                .filter(b -> b.getStatus() == BookingStatus.NO_SHOW)
                                .toList();

                // Group by client
                Map<UUID, Long> byClient = noShows.stream()
                                .filter(b -> b.getClientId() != null)
                                .collect(Collectors.groupingBy(Booking::getClientId, Collectors.counting()));

                // Repeat offenders (2+ no-shows)
                List<UUID> repeatOffenders = byClient.entrySet().stream()
                                .filter(e -> e.getValue() >= 2)
                                .map(Map.Entry::getKey)
                                .toList();

                return new NoShowReport(
                                noShows.size(),
                                byClient,
                                repeatOffenders);
        }

        /**
         * Get cancellation report.
         */
        @Transactional(readOnly = true)
        public CancellationReport getCancellationReport(UUID tenantId, LocalDate startDate, LocalDate endDate) {
                LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
                LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

                List<Booking> cancellations = bookingRepository.findByTenantIdAndDateRange(tenantId, start, end)
                                .stream()
                                .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                                .toList();

                // Group by service
                Map<UUID, Long> byService = cancellations.stream()
                                .collect(Collectors.groupingBy(Booking::getServiceId, Collectors.counting()));

                // Group by staff
                Map<UUID, Long> byStaff = cancellations.stream()
                                .filter(b -> b.getStaffId() != null)
                                .collect(Collectors.groupingBy(Booking::getStaffId, Collectors.counting()));

                return new CancellationReport(
                                cancellations.size(),
                                byService,
                                byStaff);
        }

        // Report DTOs
        public record AppointmentStats(
                        int total,
                        long confirmed,
                        long cancelled,
                        long noShow,
                        long pending,
                        Map<UUID, Long> byService,
                        Map<UUID, Long> byStaff) {
        }

        public record RevenueReport(
                        BigDecimal totalRevenue,
                        BigDecimal totalRefunds,
                        BigDecimal netRevenue,
                        int transactionCount,
                        Map<String, BigDecimal> byPaymentMethod,
                        Map<UUID, BigDecimal> byBooking) {
        }

        public record ClientStats(
                        int totalClients,
                        long newClientsThisMonth,
                        long clientsWithBookings,
                        long returningClients,
                        Map<UUID, Long> topClientsByBookings) {
        }

        public record StaffPerformance(
                        UUID staffId,
                        int totalAppointments,
                        long completed,
                        long cancelled,
                        long noShows,
                        BigDecimal revenueGenerated) {
        }

        public record NoShowReport(
                        int totalNoShows,
                        Map<UUID, Long> byClient,
                        List<UUID> repeatOffenders) {
        }

        public record CancellationReport(
                        int totalCancellations,
                        Map<UUID, Long> byService,
                        Map<UUID, Long> byStaff) {
        }
}
