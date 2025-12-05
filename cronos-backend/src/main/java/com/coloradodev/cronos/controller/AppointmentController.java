package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.dto.appointment.AppointmentRequest;
import com.coloradodev.cronos.dto.appointment.AppointmentResponseDTO;
import com.coloradodev.cronos.dto.appointment.TimeSlot;
import com.coloradodev.cronos.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/availability")
    public ResponseEntity<List<TimeSlot>> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam UUID serviceId) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(date, serviceId));
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @jakarta.validation.Valid @RequestBody AppointmentRequest request) {
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
}
