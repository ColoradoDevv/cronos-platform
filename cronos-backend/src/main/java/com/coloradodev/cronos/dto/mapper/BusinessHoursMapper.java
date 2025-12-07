package com.coloradodev.cronos.dto.mapper;

import com.coloradodev.cronos.domain.BusinessHours;
import com.coloradodev.cronos.dto.businesshours.BusinessHoursRequestDTO;
import com.coloradodev.cronos.dto.businesshours.BusinessHoursResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for BusinessHours entity.
 */
@Mapper(componentModel = "spring")
public interface BusinessHoursMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    BusinessHours toEntity(BusinessHoursRequestDTO dto);

    BusinessHoursResponseDTO toResponseDTO(BusinessHours entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    void updateEntityFromDTO(BusinessHoursRequestDTO dto, @MappingTarget BusinessHours entity);
}
