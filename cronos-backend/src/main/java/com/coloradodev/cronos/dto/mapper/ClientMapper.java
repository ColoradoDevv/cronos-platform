package com.coloradodev.cronos.dto.mapper;

import com.coloradodev.cronos.domain.Client;
import com.coloradodev.cronos.dto.client.ClientPreferencesDTO;
import com.coloradodev.cronos.dto.client.ClientRequestDTO;
import com.coloradodev.cronos.dto.client.ClientResponseDTO;
import com.coloradodev.cronos.domain.embedded.ClientPreferences;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Client entity.
 */
@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Client toEntity(ClientRequestDTO dto);

    ClientResponseDTO toResponseDTO(Client entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ClientRequestDTO dto, @MappingTarget Client entity);

    ClientPreferences toPreferencesEntity(ClientPreferencesDTO dto);
    
    ClientPreferencesDTO toPreferencesDTO(ClientPreferences entity);
}
