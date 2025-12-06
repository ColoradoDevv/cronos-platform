package com.coloradodev.cronos.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private ServiceCategory category;

    @ManyToMany(mappedBy = "services")
    private Set<Staff> staff = new HashSet<>();
}
