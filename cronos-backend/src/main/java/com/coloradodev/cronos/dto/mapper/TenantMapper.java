package com.coloradodev.cronos.dto.mapper;

import com.coloradodev.cronos.domain.Tenant;
import com.coloradodev.cronos.dto.tenant.TenantRequestDTO;
import com.coloradodev.cronos.dto.tenant.TenantResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Tenant entity.
 */
@Mapper(componentModel = "spring", uses = {SubscriptionMapper.class})
public interface TenantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "subscription", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Tenant toEntity(TenantRequestDTO dto);

    TenantResponseDTO toResponseDTO(Tenant entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "subscription", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(TenantRequestDTO dto, @MappingTarget Tenant entity);
}
