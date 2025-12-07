package com.coloradodev.cronos.service;

import com.coloradodev.cronos.domain.BusinessHours;
import com.coloradodev.cronos.exception.ResourceNotFoundException;
import com.coloradodev.cronos.repository.BusinessHoursRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing tenant business hours and availability.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessHoursService {

    private final BusinessHoursRepository businessHoursRepository;

    /**
     * Set or update business hours for a specific day of the week.
     */
    @Transactional
    public BusinessHours setBusinessHours(UUID tenantId, DayOfWeek dayOfWeek,
            LocalTime openTime, LocalTime closeTime, boolean isOpen) {
        Optional<BusinessHours> existing = businessHoursRepository.findByTenantIdAndDayOfWeek(tenantId, dayOfWeek);

        BusinessHours businessHours;
        if (existing.isPresent()) {
            businessHours = existing.get();
            businessHours.setOpenTime(openTime);
            businessHours.setCloseTime(closeTime);
            businessHours.setIsOpen(isOpen);
        } else {
            businessHours = new BusinessHours();
            businessHours.setTenantId(tenantId);
            businessHours.setDayOfWeek(dayOfWeek);
            businessHours.setOpenTime(openTime);
            businessHours.setCloseTime(closeTime);
            businessHours.setIsOpen(isOpen);
        }

        return businessHoursRepository.save(businessHours);
    }

    /**
     * Get all business hours for a tenant.
     */
    @Transactional(readOnly = true)
    public List<BusinessHours> getBusinessHours(UUID tenantId) {
        return businessHoursRepository.findByTenantId(tenantId);
    }

    /**
     * Get business hours for a specific day of the week.
     */
    @Transactional(readOnly = true)
    public Optional<BusinessHours> getBusinessHoursForDay(UUID tenantId, DayOfWeek dayOfWeek) {
        return businessHoursRepository.findByTenantIdAndDayOfWeek(tenantId, dayOfWeek);
    }

    /**
     * Get business hours for a specific date (considers day of week).
     */
    @Transactional(readOnly = true)
    public Optional<BusinessHours> getBusinessHoursForDate(UUID tenantId, LocalDate date) {
        return getBusinessHoursForDay(tenantId, date.getDayOfWeek());
    }

    /**
     * Check if the business is open at a specific date/time.
     */
    @Transactional(readOnly = true)
    public boolean isBusinessOpen(UUID tenantId, LocalDateTime dateTime) {
        Optional<BusinessHours> hoursOpt = getBusinessHoursForDay(tenantId, dateTime.getDayOfWeek());

        if (hoursOpt.isEmpty()) {
            return false; // No hours configured = closed
        }

        BusinessHours hours = hoursOpt.get();
        if (!hours.getIsOpen()) {
            return false;
        }

        LocalTime time = dateTime.toLocalTime();
        return !time.isBefore(hours.getOpenTime()) && time.isBefore(hours.getCloseTime());
    }

    /**
     * Check if a time range falls within business hours.
     */
    @Transactional(readOnly = true)
    public boolean isWithinBusinessHours(UUID tenantId, LocalDateTime startTime, LocalDateTime endTime) {
        // Must be same day
        if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
            return false;
        }

        Optional<BusinessHours> hoursOpt = getBusinessHoursForDay(tenantId, startTime.getDayOfWeek());

        if (hoursOpt.isEmpty()) {
            return false;
        }

        BusinessHours hours = hoursOpt.get();
        if (!hours.getIsOpen()) {
            return false;
        }

        LocalTime start = startTime.toLocalTime();
        LocalTime end = endTime.toLocalTime();

        return !start.isBefore(hours.getOpenTime()) && !end.isAfter(hours.getCloseTime());
    }

    /**
     * Find the next available business date starting from a given date.
     */
    @Transactional(readOnly = true)
    public LocalDate getNextAvailableDate(UUID tenantId, LocalDate fromDate) {
        LocalDate currentDate = fromDate;
        int maxDaysToCheck = 365; // Prevent infinite loop

        for (int i = 0; i < maxDaysToCheck; i++) {
            Optional<BusinessHours> hoursOpt = getBusinessHoursForDay(tenantId, currentDate.getDayOfWeek());

            if (hoursOpt.isPresent() && hoursOpt.get().getIsOpen()) {
                return currentDate;
            }

            currentDate = currentDate.plusDays(1);
        }

        // No available dates found within a year
        throw new ResourceNotFoundException("No available business dates found within the next year");
    }

    /**
     * Get all open days for a tenant.
     */
    @Transactional(readOnly = true)
    public List<BusinessHours> getOpenDays(UUID tenantId) {
        return businessHoursRepository.findByTenantIdAndIsOpen(tenantId, true);
    }

    /**
     * Initialize default business hours for a new tenant (Mon-Fri 9-17).
     */
    @Transactional
    public void initializeDefaultHours(UUID tenantId) {
        LocalTime defaultOpen = LocalTime.of(9, 0);
        LocalTime defaultClose = LocalTime.of(17, 0);

        for (DayOfWeek day : DayOfWeek.values()) {
            boolean isWeekday = day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
            setBusinessHours(tenantId, day, defaultOpen, defaultClose, isWeekday);
        }

        log.info("Initialized default business hours for tenant {}", tenantId);
    }
}
