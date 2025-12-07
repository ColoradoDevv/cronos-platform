package com.coloradodev.cronos.service;

import com.coloradodev.cronos.domain.Appointment;
import com.coloradodev.cronos.domain.Service;
import com.coloradodev.cronos.domain.Tenant;
import com.coloradodev.cronos.dto.appointment.TimeSlot;
import com.coloradodev.cronos.repository.AppointmentRepository;
import com.coloradodev.cronos.repository.ServiceRepository;
import com.coloradodev.cronos.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

        @Mock
        private AppointmentRepository appointmentRepository;

        @Mock
        private ServiceRepository serviceRepository;

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private AppointmentService appointmentService;

        private Service mockService;
        private Tenant mockTenant;
        private UUID serviceId;

        @BeforeEach
        void setUp() {
                serviceId = UUID.randomUUID();

                mockTenant = Tenant.builder()
                                .id(UUID.randomUUID())
                                .name("Test Tenant")
                                .workDayStart(LocalTime.of(9, 0))
                                .workDayEnd(LocalTime.of(12, 0)) // Short day for easier testing
                                .build();

                mockService = Service.builder()
                                .id(serviceId)
                                .name("Test Service")
                                .duration(60) // 1 hour duration
                                .tenant(mockTenant)
                                .build();
        }

        @Test
        void shouldReturnSlotsWhenNoAppointments() {
                // Given
                LocalDate date = LocalDate.now().plusDays(1);
                when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(mockService));
                when(appointmentRepository.findByServiceIdAndDateRange(any(), any(), any()))
                                .thenReturn(Collections.emptyList());

                // When
                List<TimeSlot> slots = appointmentService.getAvailableSlots(date, serviceId);

                // Then
                // 9:00-10:00, 10:00-11:00, 11:00-12:00 -> 3 slots
                assertEquals(3, slots.size());
                assertEquals(LocalDateTime.of(date, LocalTime.of(9, 0)), slots.get(0).getStartTime());
        }

        @Test
        void shouldDetectOverlapAndExcludeSlot() {
                // Given
                LocalDate date = LocalDate.now().plusDays(1);

                // Create an existing appointment at 10:00
                Appointment existingAppointment = Appointment.builder()
                                .startTime(LocalDateTime.of(date, LocalTime.of(10, 0)))
                                .endTime(LocalDateTime.of(date, LocalTime.of(11, 0)))
                                .build();

                when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(mockService));
                when(appointmentRepository.findByServiceIdAndDateRange(any(), any(), any()))
                                .thenReturn(List.of(existingAppointment));

                // When
                List<TimeSlot> slots = appointmentService.getAvailableSlots(date, serviceId);

                // Then
                // 9:00-10:00 (Available)
                // 10:00-11:00 (Booked - Excluded)
                // 11:00-12:00 (Available)

                assertEquals(2, slots.size());

                // Slot 1: 9:00
                assertEquals(LocalDateTime.of(date, LocalTime.of(9, 0)), slots.get(0).getStartTime());

                // Slot 2: 11:00 (Skipped 10:00)
                assertEquals(LocalDateTime.of(date, LocalTime.of(11, 0)), slots.get(1).getStartTime());
        }
}
