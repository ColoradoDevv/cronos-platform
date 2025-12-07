package com.coloradodev.cronos.repository;

import com.coloradodev.cronos.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    List<Client> findByTenantId(UUID tenantId);

    Optional<Client> findByTenantIdAndId(UUID tenantId, UUID id);

    Optional<Client> findByTenantIdAndEmail(UUID tenantId, String email);

    Optional<Client> findByTenantIdAndPhone(UUID tenantId, String phone);

    List<Client> findByTenantIdAndUserId(UUID tenantId, UUID userId);

    boolean existsByTenantIdAndEmail(UUID tenantId, String email);

    @Query("SELECT c FROM Client c WHERE c.tenantId = :tenantId " +
            "AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Client> searchByName(@Param("tenantId") UUID tenantId, @Param("query") String query);
}
