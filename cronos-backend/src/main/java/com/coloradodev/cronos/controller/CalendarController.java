package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.core.tenant.TenantContext;
import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.dto.appointment.TimeSlot;
import com.coloradodev.cronos.dto.booking.BookingResponseDTO;
import com.coloradodev.cronos.dto.mapper.BookingMapper;
import com.coloradodev.cronos.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for calendar operations and availability.
 */
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;
    private final BookingMapper bookingMapper;

    /**
     * Get calendar view with all bookings for a date range.
     */
    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getCalendarView(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) UUID staffId) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        List<Booking> bookings = calendarService.getCalendarView(tenantId, start, end, staffId);
        List<BookingResponseDTO> response = bookings.stream()
                .map(bookingMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Get available time slots for a service on a specific date.
     */
    @GetMapping("/availability")
    public ResponseEntity<List<TimeSlot>> getAvailability(
            @RequestParam UUID serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) UUID staffId) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        List<TimeSlot> slots = calendarService.getAvailableSlots(tenantId, serviceId, date, staffId);
        return ResponseEntity.ok(slots);
    }

    /**
     * Find conflicting bookings for a time range.
     */
    @GetMapping("/conflicts")
    public ResponseEntity<List<BookingResponseDTO>> getConflicts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) UUID staffId) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        List<Booking> conflicts = calendarService.getConflicts(tenantId, start, end, staffId);
        List<BookingResponseDTO> response = conflicts.stream()
                .map(bookingMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }
}
