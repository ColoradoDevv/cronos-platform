package com.coloradodev.cronos.dto.mapper;

import com.coloradodev.cronos.domain.Service;
import com.coloradodev.cronos.dto.service.ServicePublicDTO;
import com.coloradodev.cronos.dto.service.ServiceRequestDTO;
import com.coloradodev.cronos.dto.service.ServiceResponseDTO;
import com.coloradodev.cronos.dto.service.ServiceSummaryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Service entity.
 */
@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "staff", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Service toEntity(ServiceRequestDTO dto);

    @Mapping(source = "category.name", target = "categoryName")
    ServiceResponseDTO toResponseDTO(Service entity);

    @Mapping(source = "category.name", target = "categoryName")
    ServicePublicDTO toPublicDTO(Service entity);

    ServiceSummaryDTO toSummaryDTO(Service entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "staff", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ServiceRequestDTO dto, @MappingTarget Service entity);
}
