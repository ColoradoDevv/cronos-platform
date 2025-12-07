package com.coloradodev.cronos.dto.mapper;

import com.coloradodev.cronos.domain.ServiceCategory;
import com.coloradodev.cronos.dto.category.ServiceCategoryRequestDTO;
import com.coloradodev.cronos.dto.category.ServiceCategoryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for ServiceCategory entity.
 */
@Mapper(componentModel = "spring")
public interface ServiceCategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ServiceCategory toEntity(ServiceCategoryRequestDTO dto);

    ServiceCategoryResponseDTO toResponseDTO(ServiceCategory entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ServiceCategoryRequestDTO dto, @MappingTarget ServiceCategory entity);
}
