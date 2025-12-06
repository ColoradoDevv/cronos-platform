package com.coloradodev.cronos.repository;

import com.coloradodev.cronos.domain.Subscription;
import com.coloradodev.cronos.domain.Subscription.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findByTenantId(UUID tenantId);

    Optional<Subscription> findByTenantIdAndStatus(UUID tenantId, SubscriptionStatus status);
}
