package com.coloradodev.cronos.service;

import com.coloradodev.cronos.domain.Appointment;
import com.coloradodev.cronos.domain.Service;
import com.coloradodev.cronos.domain.User;
import com.coloradodev.cronos.dto.appointment.AppointmentRequest;
import com.coloradodev.cronos.dto.appointment.TimeSlot;
import com.coloradodev.cronos.repository.AppointmentRepository;
import com.coloradodev.cronos.repository.ServiceRepository;
import com.coloradodev.cronos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public List<TimeSlot> getAvailableSlots(LocalDate date, UUID serviceId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        int durationMinutes = service.getDuration();
        LocalDateTime startOfDay = LocalDateTime.of(date, START_WORK_TIME);
        LocalDateTime endOfDay = LocalDateTime.of(date, END_WORK_TIME);

        // Fetch existing appointments for the day
        List<Appointment> existingAppointments = appointmentRepository.findByServiceIdAndDateRange(
                serviceId, startOfDay, endOfDay);

        List<TimeSlot> availableSlots = new ArrayList<>();
        LocalDateTime currentSlotStart = startOfDay;

        while (currentSlotStart.plusMinutes(durationMinutes).isBefore(endOfDay) ||
                currentSlotStart.plusMinutes(durationMinutes).isEqual(endOfDay)) {

            LocalDateTime currentSlotEnd = currentSlotStart.plusMinutes(durationMinutes);

            boolean isOverlapping = false;
            for (Appointment appointment : existingAppointments) {
                if (isOverlapping(currentSlotStart, currentSlotEnd, appointment.getStartTime(),
                        appointment.getEndTime())) {
                    isOverlapping = true;
                    break;
                }
            }

            if (!isOverlapping) {
                availableSlots.add(TimeSlot.builder()
                        .startTime(currentSlotStart)
                        .endTime(currentSlotEnd)
                        .build());
            }

            currentSlotStart = currentSlotStart.plusMinutes(durationMinutes);
        }

        return availableSlots;
    }

    @Transactional
    public Appointment createAppointment(AppointmentRequest request) {
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        LocalDateTime endTime = request.getStartTime().plusMinutes(service.getDuration());

        // Double check availability
        List<Appointment> overlapping = appointmentRepository.findOverlappingAppointments(
                request.getStartTime(), endTime, request.getServiceId());

        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("Slot is already booked");
        }

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Appointment appointment = Appointment.builder()
                .startTime(request.getStartTime())
                .endTime(endTime)
                .status("CONFIRMED") // Default status
                .service(service)
                .user(user)
                .build();

        return appointmentRepository.save(appointment);
    }

    private boolean isOverlapping(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
