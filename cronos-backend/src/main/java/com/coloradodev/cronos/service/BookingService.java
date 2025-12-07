package com.coloradodev.cronos.service;

import com.coloradodev.cronos.domain.Appointment;
import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.domain.Booking.BookingStatus;
import com.coloradodev.cronos.domain.Client;
import com.coloradodev.cronos.domain.Staff;
import com.coloradodev.cronos.dto.booking.BookingRequestDTO;
import com.coloradodev.cronos.exception.BusinessRuleException;
import com.coloradodev.cronos.exception.ResourceNotFoundException;
import com.coloradodev.cronos.exception.SlotNotAvailableException;
import com.coloradodev.cronos.repository.AppointmentRepository;
import com.coloradodev.cronos.repository.BookingRepository;
import com.coloradodev.cronos.repository.ServiceRepository;
import com.coloradodev.cronos.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing bookings and the booking workflow.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;
    private final TenantRepository tenantRepository;
    private final ClientService clientService;
    private final CalendarService calendarService;
    private final StaffService staffService;
    private final AuditService auditService;

    /**
     * Create a public booking (from the booking widget).
     */
    @Transactional
    public Booking createPublicBooking(String tenantSlug, BookingRequestDTO request) {
        // Get tenant by slug
        var tenant = tenantRepository.findBySlug(tenantSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant with slug", tenantSlug));
        UUID tenantId = tenant.getId();

        // Validate service exists
        var service = serviceRepository.findByTenantIdAndId(tenantId, request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", request.getServiceId().toString()));

        LocalDateTime endTime = request.getStartTime().plusMinutes(service.getDuration());

        // Validate time slot is available
        if (!calendarService.checkSlotAvailability(tenantId, request.getServiceId(), request.getStartTime(),
                request.getStaffId())) {
            throw new SlotNotAvailableException("The requested time slot is no longer available");
        }

        // Find or create client
        Client client = clientService.findOrCreateClient(
                tenantId,
                request.getClientName() != null ? request.getClientName().split(" ")[0] : "Guest",
                request.getClientName() != null && request.getClientName().contains(" ")
                        ? request.getClientName().substring(request.getClientName().indexOf(" ") + 1)
                        : "",
                request.getClientEmail(),
                request.getClientPhone());

        // Assign staff if not specified - pick first available
        UUID staffId = request.getStaffId();
        if (staffId == null) {
            List<Staff> availableStaff = calendarService.getAvailableStaffForSlot(
                    tenantId, request.getServiceId(), request.getStartTime(), endTime);
            if (!availableStaff.isEmpty()) {
                staffId = availableStaff.get(0).getId();
            }
        }

        // Create booking
        Booking booking = new Booking();
        booking.setTenantId(tenantId);
        booking.setServiceId(request.getServiceId());
        booking.setStaffId(staffId);
        booking.setClientId(client.getId());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(endTime);
        booking.setStatus(BookingStatus.PENDING);
        booking.setClientName(request.getClientName());
        booking.setClientEmail(request.getClientEmail());
        booking.setClientPhone(request.getClientPhone());
        booking.setNotes(request.getNotes());

        Booking saved = bookingRepository.save(booking);

        auditService.logCreate(tenantId, null, "Booking", saved.getId(),
                Map.of("service", service.getName(),
                        "startTime", request.getStartTime().toString(),
                        "client", client.getFirstName() + " " + client.getLastName()));

        log.info("Created booking {} for tenant {}", saved.getId(), tenantId);

        // TODO: Send confirmation email via NotificationService

        return saved;
    }

    /**
     * Confirm a pending booking.
     */
    @Transactional
    public Booking confirmBooking(UUID tenantId, UUID bookingId) {
        Booking booking = getBookingById(tenantId, bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessRuleException("INVALID_STATUS",
                    "Only pending bookings can be confirmed. Current status: " + booking.getStatus());
        }

        // Create appointment
        Appointment appointment = Appointment.builder()
                .service(booking.getService())
                .tenantId(tenantId)
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status("CONFIRMED")
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Update booking
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setAppointmentId(savedAppointment.getId());
        Booking saved = bookingRepository.save(booking);

        auditService.logAction(tenantId, null, "CONFIRM", "Booking", bookingId,
                Map.of("status", "PENDING"),
                Map.of("status", "CONFIRMED", "appointmentId", savedAppointment.getId()));

        log.info("Confirmed booking {}", bookingId);

        // TODO: Send confirmation notification

        return saved;
    }

    /**
     * Cancel a booking.
     */
    @Transactional
    public Booking cancelBooking(UUID tenantId, UUID bookingId, String reason) {
        Booking booking = getBookingById(tenantId, bookingId);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessRuleException("ALREADY_CANCELLED", "Booking is already cancelled");
        }

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);
        if (reason != null) {
            String notes = booking.getNotes();
            booking.setNotes((notes != null ? notes + "\n" : "") + "Cancellation reason: " + reason);
        }

        Booking saved = bookingRepository.save(booking);

        auditService.logAction(tenantId, null, "CANCEL", "Booking", bookingId,
                Map.of("status", oldStatus.toString()),
                Map.of("status", "CANCELLED", "reason", reason != null ? reason : ""));

        log.info("Cancelled booking {} with reason: {}", bookingId, reason);

        // TODO: Send cancellation notification

        return saved;
    }

    /**
     * Reschedule a booking to a new time.
     */
    @Transactional
    public Booking rescheduleBooking(UUID tenantId, UUID bookingId, LocalDateTime newStartTime) {
        Booking booking = getBookingById(tenantId, bookingId);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessRuleException("CANCELLED_BOOKING", "Cannot reschedule a cancelled booking");
        }

        // Calculate new end time based on service duration
        var service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", booking.getServiceId().toString()));

        LocalDateTime newEndTime = newStartTime.plusMinutes(service.getDuration());

        // Check if new slot is available (excluding current booking)
        List<Booking> conflicts = bookingRepository.findOverlappingBookingsExcluding(
                tenantId, booking.getStaffId(), newStartTime, newEndTime, bookingId);

        if (!conflicts.isEmpty()) {
            throw new SlotNotAvailableException("The new time slot is not available");
        }

        LocalDateTime oldStartTime = booking.getStartTime();
        booking.setStartTime(newStartTime);
        booking.setEndTime(newEndTime);

        Booking saved = bookingRepository.save(booking);

        auditService.logAction(tenantId, null, "RESCHEDULE", "Booking", bookingId,
                Map.of("startTime", oldStartTime.toString()),
                Map.of("startTime", newStartTime.toString()));

        log.info("Rescheduled booking {} from {} to {}", bookingId, oldStartTime, newStartTime);

        // TODO: Send reschedule notification

        return saved;
    }

    /**
     * Get booking by ID.
     */
    @Transactional(readOnly = true)
    public Booking getBookingById(UUID tenantId, UUID bookingId) {
        return bookingRepository.findByTenantIdAndId(tenantId, bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId.toString()));
    }

    /**
     * Get bookings for a tenant with optional status filter.
     */
    @Transactional(readOnly = true)
    public Page<Booking> getBookingsByTenant(UUID tenantId, BookingStatus status, Pageable pageable) {
        List<Booking> bookings = status != null
                ? bookingRepository.findByTenantIdAndStatus(tenantId, status)
                : bookingRepository.findByTenantId(tenantId);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), bookings.size());

        if (start > bookings.size()) {
            return new PageImpl<>(List.of(), pageable, bookings.size());
        }

        return new PageImpl<>(bookings.subList(start, end), pageable, bookings.size());
    }

    /**
     * Get bookings for a specific client.
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByClient(UUID tenantId, UUID clientId) {
        // Verify client exists
        clientService.getClientById(tenantId, clientId);
        return bookingRepository.findByTenantIdAndClientIdOrderByCreatedAtDesc(tenantId, clientId);
    }

    /**
     * Mark a booking as no-show.
     */
    @Transactional
    public Booking markAsNoShow(UUID tenantId, UUID bookingId) {
        Booking booking = getBookingById(tenantId, bookingId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessRuleException("INVALID_STATUS",
                    "Only confirmed bookings can be marked as no-show");
        }

        booking.setStatus(BookingStatus.NO_SHOW);
        Booking saved = bookingRepository.save(booking);

        auditService.logAction(tenantId, null, "NO_SHOW", "Booking", bookingId);

        log.info("Marked booking {} as no-show", bookingId);
        return saved;
    }
}
