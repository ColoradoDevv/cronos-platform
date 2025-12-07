package com.coloradodev.cronos.repository;

import com.coloradodev.cronos.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    List<Client> findByTenantId(UUID tenantId);

    Optional<Client> findByTenantIdAndId(UUID tenantId, UUID id);

    Optional<Client> findByTenantIdAndEmail(UUID tenantId, String email);

    List<Client> findByTenantIdAndUserId(UUID tenantId, UUID userId);

    boolean existsByTenantIdAndEmail(UUID tenantId, String email);
}
