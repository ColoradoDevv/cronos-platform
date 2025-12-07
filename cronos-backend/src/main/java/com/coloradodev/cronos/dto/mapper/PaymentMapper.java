package com.coloradodev.cronos.dto.mapper;

import com.coloradodev.cronos.domain.Payment;
import com.coloradodev.cronos.dto.payment.PaymentRequestDTO;
import com.coloradodev.cronos.dto.payment.PaymentResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Payment entity.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toEntity(PaymentRequestDTO dto);

    PaymentResponseDTO toResponseDTO(Payment entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(PaymentRequestDTO dto, @MappingTarget Payment entity);
}
