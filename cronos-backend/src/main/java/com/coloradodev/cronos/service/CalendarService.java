package com.coloradodev.cronos.service;

import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.domain.BusinessHours;
import com.coloradodev.cronos.domain.Staff;
import com.coloradodev.cronos.dto.appointment.TimeSlot;
import com.coloradodev.cronos.exception.ResourceNotFoundException;
import com.coloradodev.cronos.repository.BookingRepository;
import com.coloradodev.cronos.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for calendar operations, slot availability, and scheduling logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final BusinessHoursService businessHoursService;
    private final StaffService staffService;

    /**
     * Get available time slots for booking.
     *
     * @param tenantId  The tenant
     * @param serviceId The service to book
     * @param date      The date to check
     * @param staffId   Optional specific staff member
     * @return List of available time slots
     */
    @Transactional(readOnly = true)
    public List<TimeSlot> getAvailableSlots(UUID tenantId, UUID serviceId, LocalDate date, UUID staffId) {
        // Get service
        com.coloradodev.cronos.domain.Service service = serviceRepository.findByTenantIdAndId(tenantId, serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service", serviceId.toString()));

        int durationMinutes = service.getDuration();

        // Get business hours for the day
        Optional<BusinessHours> businessHoursOpt = businessHoursService.getBusinessHoursForDate(tenantId, date);
        if (businessHoursOpt.isEmpty() || !businessHoursOpt.get().getIsOpen()) {
            return List.of(); // Business closed this day
        }

        BusinessHours businessHours = businessHoursOpt.get();
        LocalDateTime startOfDay = LocalDateTime.of(date, businessHours.getOpenTime());
        LocalDateTime endOfDay = LocalDateTime.of(date, businessHours.getCloseTime());

        // Get staff members who can provide this service
        List<Staff> availableStaff = staffId != null
                ? List.of(staffService.getStaffById(tenantId, staffId))
                : staffService.getStaffForService(tenantId, serviceId);

        if (availableStaff.isEmpty()) {
            return List.of(); // No staff available for this service
        }

        // Generate potential time slots
        List<TimeSlot> allSlots = new ArrayList<>();
        LocalDateTime currentSlotStart = startOfDay;

        while (!currentSlotStart.plusMinutes(durationMinutes).isAfter(endOfDay)) {
            LocalDateTime slotEnd = currentSlotStart.plusMinutes(durationMinutes);

            // Check availability for at least one staff member
            for (Staff staff : availableStaff) {
                if (staff.getIsActive()
                        && isSlotAvailableForStaff(tenantId, staff.getId(), currentSlotStart, slotEnd)) {
                    allSlots.add(TimeSlot.builder()
                            .startTime(currentSlotStart)
                            .endTime(slotEnd)
                            .build());
                    break; // One available staff is enough
                }
            }

            currentSlotStart = currentSlotStart.plusMinutes(durationMinutes);
        }

        return allSlots;
    }

    /**
     * Get calendar view with all bookings for a date range.
     */
    @Transactional(readOnly = true)
    public List<Booking> getCalendarView(UUID tenantId, LocalDate startDate, LocalDate endDate, UUID staffId) {
        LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByTenantIdAndDateRange(tenantId, start, end);

        if (staffId != null) {
            bookings = bookings.stream()
                    .filter(b -> staffId.equals(b.getStaffId()))
                    .collect(Collectors.toList());
        }

        return bookings;
    }

    /**
     * Check if a specific slot is available.
     */
    @Transactional(readOnly = true)
    public boolean checkSlotAvailability(UUID tenantId, UUID serviceId, LocalDateTime startTime, UUID staffId) {
        com.coloradodev.cronos.domain.Service service = serviceRepository.findByTenantIdAndId(tenantId, serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service", serviceId.toString()));

        LocalDateTime endTime = startTime.plusMinutes(service.getDuration());

        // Check business hours
        if (!businessHoursService.isWithinBusinessHours(tenantId, startTime, endTime)) {
            return false;
        }

        // Check staff availability
        if (staffId != null) {
            return isSlotAvailableForStaff(tenantId, staffId, startTime, endTime);
        }

        // Check if any staff can handle this
        List<Staff> availableStaff = staffService.getStaffForService(tenantId, serviceId);
        for (Staff staff : availableStaff) {
            if (staff.getIsActive() && isSlotAvailableForStaff(tenantId, staff.getId(), startTime, endTime)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get staff schedule for a date range.
     */
    @Transactional(readOnly = true)
    public List<Booking> getStaffSchedule(UUID tenantId, UUID staffId, LocalDate startDate, LocalDate endDate) {
        // Verify staff exists
        staffService.getStaffById(tenantId, staffId);

        LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

        return bookingRepository.findActiveBookingsForStaffInRange(tenantId, staffId, start, end);
    }

    /**
     * Find conflicting bookings for a time range.
     */
    @Transactional(readOnly = true)
    public List<Booking> getConflicts(UUID tenantId, LocalDateTime startTime, LocalDateTime endTime, UUID staffId) {
        if (staffId == null) {
            return List.of();
        }
        return bookingRepository.findOverlappingBookings(tenantId, staffId, startTime, endTime);
    }

    /**
     * Find first available slot for a service starting from a date.
     */
    @Transactional(readOnly = true)
    public Optional<TimeSlot> findNextAvailableSlot(UUID tenantId, UUID serviceId, LocalDate fromDate,
            int maxDaysToSearch) {
        for (int i = 0; i < maxDaysToSearch; i++) {
            LocalDate date = fromDate.plusDays(i);
            List<TimeSlot> slots = getAvailableSlots(tenantId, serviceId, date, null);
            if (!slots.isEmpty()) {
                return Optional.of(slots.get(0));
            }
        }
        return Optional.empty();
    }

    /**
     * Get available staff for a specific time slot.
     */
    @Transactional(readOnly = true)
    public List<Staff> getAvailableStaffForSlot(UUID tenantId, UUID serviceId, LocalDateTime startTime,
            LocalDateTime endTime) {
        List<Staff> staffForService = staffService.getStaffForService(tenantId, serviceId);

        return staffForService.stream()
                .filter(Staff::getIsActive)
                .filter(staff -> isSlotAvailableForStaff(tenantId, staff.getId(), startTime, endTime))
                .collect(Collectors.toList());
    }

    /**
     * Check if a slot is available for a specific staff member.
     */
    private boolean isSlotAvailableForStaff(UUID tenantId, UUID staffId, LocalDateTime startTime,
            LocalDateTime endTime) {
        List<Booking> conflicts = bookingRepository.findOverlappingBookings(tenantId, staffId, startTime, endTime);
        return conflicts.isEmpty();
    }
}
