package com.coloradodev.cronos.dto.mapper;

import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.dto.booking.BookingRequestDTO;
import com.coloradodev.cronos.dto.booking.BookingResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Booking entity.
 */
@Mapper(componentModel = "spring", uses = {ServiceMapper.class, StaffMapper.class})
public interface BookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "staff", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "appointmentId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Booking toEntity(BookingRequestDTO dto);

    @Mapping(source = "service", target = "service")
    @Mapping(source = "staff", target = "staff")
    BookingResponseDTO toResponseDTO(Booking entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "staff", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "appointmentId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(BookingRequestDTO dto, @MappingTarget Booking entity);
}
