package com.coloradodev.cronos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

import jakarta.persistence.EntityListeners;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "services")
@EntityListeners(TenantEntityListener.class)
public class Service extends BaseEntity implements TenantAware {

    private String name;
    private String description;
    private Integer duration; // in minutes
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}
