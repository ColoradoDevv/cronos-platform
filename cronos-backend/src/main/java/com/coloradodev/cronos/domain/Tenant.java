package com.coloradodev.cronos.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tenants")
public class Tenant extends BaseEntity {

    private String name;

    @Column(unique = true)
    private String slug;

    private String status;

    // White Label fields
    private String primaryColor;
    private String logoUrl;

    @Column(name = "work_day_start")
    private java.time.LocalTime workDayStart;

    @Column(name = "work_day_end")
    private java.time.LocalTime workDayEnd;

    // Subscription relationship
    @OneToOne(mappedBy = "tenant", fetch = FetchType.LAZY)
    private Subscription subscription;
}
