package com.coloradodev.cronos.dto.mapper;

import com.coloradodev.cronos.domain.Staff;
import com.coloradodev.cronos.dto.staff.StaffRequestDTO;
import com.coloradodev.cronos.dto.staff.StaffResponseDTO;
import com.coloradodev.cronos.dto.staff.StaffSummaryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Staff entity.
 */
@Mapper(componentModel = "spring", uses = {ServiceMapper.class})
public interface StaffMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "services", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Staff toEntity(StaffRequestDTO dto);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.email", target = "email")
    StaffResponseDTO toResponseDTO(Staff entity);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    StaffSummaryDTO toSummaryDTO(Staff entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "services", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(StaffRequestDTO dto, @MappingTarget Staff entity);
}
