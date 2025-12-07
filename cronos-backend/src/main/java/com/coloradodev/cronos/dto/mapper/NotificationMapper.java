package com.coloradodev.cronos.dto.mapper;

import com.coloradodev.cronos.domain.Notification;
import com.coloradodev.cronos.dto.notification.NotificationResponseDTO;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for Notification entity.
 * Read-only mapper since notifications are system-generated.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponseDTO toResponseDTO(Notification entity);
}
