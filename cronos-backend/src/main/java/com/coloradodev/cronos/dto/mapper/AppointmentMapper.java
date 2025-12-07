package com.coloradodev.cronos.dto.mapper;

import com.coloradodev.cronos.domain.Appointment;
import com.coloradodev.cronos.dto.appointment.AppointmentRequest;
import com.coloradodev.cronos.dto.appointment.AppointmentResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Appointment entity.
 */
@Mapper(componentModel = "spring", uses = {ServiceMapper.class, StaffMapper.class, ClientMapper.class})
public interface AppointmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "staff", ignore = true)
    @Mapping(target = "staffId", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Appointment toEntity(AppointmentRequest dto);

    AppointmentResponseDTO toResponseDTO(Appointment entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "staff", ignore = true)
    @Mapping(target = "staffId", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(AppointmentRequest dto, @MappingTarget Appointment entity);
}
