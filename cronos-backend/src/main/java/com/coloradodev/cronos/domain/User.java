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

import jakarta.persistence.EntityListeners;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(TenantEntityListener.class)
public class User extends BaseEntity implements TenantAware {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}
