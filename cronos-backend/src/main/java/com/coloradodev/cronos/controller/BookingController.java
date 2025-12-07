package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.dto.appointment.TimeSlot;
import com.coloradodev.cronos.dto.booking.BookingRequestDTO;
import com.coloradodev.cronos.dto.booking.BookingResponseDTO;
import com.coloradodev.cronos.dto.mapper.BookingMapper;
import com.coloradodev.cronos.dto.mapper.ServiceMapper;
import com.coloradodev.cronos.dto.service.ServicePublicDTO;
import com.coloradodev.cronos.repository.ServiceRepository;
import com.coloradodev.cronos.repository.TenantRepository;
import com.coloradodev.cronos.exception.ResourceNotFoundException;
import com.coloradodev.cronos.service.BookingService;
import com.coloradodev.cronos.service.CalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for public booking endpoints (no authentication required).
 * Uses tenant slug to identify the tenant.
 */
@RestController
@RequestMapping("/public/{slug}")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final CalendarService calendarService;
    private final ServiceRepository serviceRepository;
    private final TenantRepository tenantRepository;
    private final BookingMapper bookingMapper;
    private final ServiceMapper serviceMapper;

    // ==================== Public Booking Endpoints ====================

    /**
     * Create a new public booking.
     */
    @PostMapping("/bookings")
    public ResponseEntity<BookingResponseDTO> createBooking(
            @PathVariable String slug,
            @Valid @RequestBody BookingRequestDTO request) {
        Booking booking = bookingService.createPublicBooking(slug, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingMapper.toResponseDTO(booking));
    }

    /**
     * Get a booking by ID (for confirmation page).
     */
    @GetMapping("/bookings/{id}")
    public ResponseEntity<BookingResponseDTO> getBooking(
            @PathVariable String slug,
            @PathVariable UUID id) {
        UUID tenantId = getTenantIdBySlug(slug);
        Booking booking = bookingService.getBookingById(tenantId, id);
        return ResponseEntity.ok(bookingMapper.toResponseDTO(booking));
    }

    /**
     * Cancel a booking (public).
     */
    @PatchMapping("/bookings/{id}/cancel")
    public ResponseEntity<BookingResponseDTO> cancelBooking(
            @PathVariable String slug,
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> body) {
        UUID tenantId = getTenantIdBySlug(slug);
        String reason = body != null ? body.get("reason") : null;
        Booking booking = bookingService.cancelBooking(tenantId, id, reason);
        return ResponseEntity.ok(bookingMapper.toResponseDTO(booking));
    }

    // ==================== Public Service Info ====================

    /**
     * Get public services list for the booking widget.
     */
    @GetMapping("/services")
    public ResponseEntity<List<ServicePublicDTO>> getPublicServices(@PathVariable String slug) {
        UUID tenantId = getTenantIdBySlug(slug);
        var services = serviceRepository.findByTenantIdAndIsActiveOrderByNameAsc(tenantId, true);
        List<ServicePublicDTO> response = services.stream()
                .map(serviceMapper::toPublicDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    // ==================== Public Availability ====================

    /**
     * Get available time slots for a service on a specific date.
     */
    @GetMapping("/availability")
    public ResponseEntity<List<TimeSlot>> getAvailability(
            @PathVariable String slug,
            @RequestParam UUID serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) UUID staffId) {
        UUID tenantId = getTenantIdBySlug(slug);
        List<TimeSlot> slots = calendarService.getAvailableSlots(tenantId, serviceId, date, staffId);
        return ResponseEntity.ok(slots);
    }

    // ==================== Helper Methods ====================

    private UUID getTenantIdBySlug(String slug) {
        return tenantRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant with slug", slug))
                .getId();
    }
}
