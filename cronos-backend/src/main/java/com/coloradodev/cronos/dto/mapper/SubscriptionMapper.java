package com.coloradodev.cronos.dto.mapper;

import com.coloradodev.cronos.domain.Subscription;
import com.coloradodev.cronos.dto.subscription.SubscriptionRequestDTO;
import com.coloradodev.cronos.dto.subscription.SubscriptionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Subscription entity.
 */
@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Subscription toEntity(SubscriptionRequestDTO dto);

    SubscriptionResponseDTO toResponseDTO(Subscription entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(SubscriptionRequestDTO dto, @MappingTarget Subscription entity);
}
