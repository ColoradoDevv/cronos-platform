package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.core.tenant.TenantContext;
import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.domain.Booking.BookingStatus;
import com.coloradodev.cronos.dto.appointment.AppointmentRequest;
import com.coloradodev.cronos.dto.appointment.AppointmentResponseDTO;
import com.coloradodev.cronos.dto.appointment.TimeSlot;
import com.coloradodev.cronos.dto.booking.BookingResponseDTO;
import com.coloradodev.cronos.dto.mapper.BookingMapper;
import com.coloradodev.cronos.repository.BookingRepository;
import com.coloradodev.cronos.service.AppointmentService;
import com.coloradodev.cronos.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for managing appointments (admin endpoints).
 * Uses the Booking entity which represents scheduled appointments.
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    // ==================== Legacy Endpoints ====================

    @GetMapping("/availability")
    public ResponseEntity<List<TimeSlot>> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam UUID serviceId) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(date, serviceId));
    }

    @PostMapping("/legacy")
    public ResponseEntity<AppointmentResponseDTO> createAppointmentLegacy(
            @Valid @RequestBody AppointmentRequest request) {
        var appointment = appointmentService.createAppointment(request);
        var response = AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .serviceId(appointment.getService().getId())
                .serviceName(appointment.getService().getName())
                .userId(appointment.getUser().getId())
                .userName(appointment.getUser().getFirstName() + " " + appointment.getUser().getLastName())
                .build();
        return ResponseEntity.ok(response);
    }

    // ==================== Booking-based Appointment Endpoints ====================

    /**
     * Get all appointments (bookings) with pagination.
     */
    @GetMapping
    public ResponseEntity<Page<BookingResponseDTO>> getAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) BookingStatus status) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<Booking> bookings = bookingService.getBookingsByTenant(tenantId, status, pageable);
        Page<BookingResponseDTO> response = bookings.map(bookingMapper::toResponseDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific appointment (booking) by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getAppointment(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Booking booking = bookingService.getBookingById(tenantId, id);
        return ResponseEntity.ok(bookingMapper.toResponseDTO(booking));
    }

    /**
     * Delete/cancel an appointment.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        bookingService.cancelBooking(tenantId, id, "Deleted by admin");
    }

    // ==================== Status Updates ====================

    /**
     * Confirm a pending appointment.
     */
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<BookingResponseDTO> confirmAppointment(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Booking booking = bookingService.confirmBooking(tenantId, id);
        return ResponseEntity.ok(bookingMapper.toResponseDTO(booking));
    }

    /**
     * Cancel an appointment.
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDTO> cancelAppointment(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> body) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        String reason = body != null ? body.get("reason") : null;
        Booking booking = bookingService.cancelBooking(tenantId, id, reason);
        return ResponseEntity.ok(bookingMapper.toResponseDTO(booking));
    }

    /**
     * Mark an appointment as complete.
     * Note: This updates status to CONFIRMED (completed state).
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<BookingResponseDTO> completeAppointment(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        // For now, completing means confirming - in future could add COMPLETED status
        Booking booking = bookingService.confirmBooking(tenantId, id);
        return ResponseEntity.ok(bookingMapper.toResponseDTO(booking));
    }

    /**
     * Mark an appointment as no-show.
     */
    @PatchMapping("/{id}/no-show")
    public ResponseEntity<BookingResponseDTO> markNoShow(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Booking booking = bookingService.markAsNoShow(tenantId, id);
        return ResponseEntity.ok(bookingMapper.toResponseDTO(booking));
    }

    // ==================== Filtered Views ====================

    /**
     * Get upcoming appointments (today and future).
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<BookingResponseDTO>> getUpcomingAppointments() {
        UUID tenantId = TenantContext.getCurrentTenantId();
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByTenantIdAndStartTimeAfterOrderByStartTimeAsc(tenantId, now);
        List<BookingResponseDTO> response = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING || b.getStatus() == BookingStatus.CONFIRMED)
                .map(bookingMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Get past appointments.
     */
    @GetMapping("/past")
    public ResponseEntity<List<BookingResponseDTO>> getPastAppointments(
            @RequestParam(defaultValue = "30") int days) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromDate = now.minusDays(days);
        List<Booking> bookings = bookingRepository.findByTenantIdAndStartTimeBetweenOrderByStartTimeDesc(
                tenantId, fromDate, now);
        List<BookingResponseDTO> response = bookings.stream()
                .map(bookingMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }
}
