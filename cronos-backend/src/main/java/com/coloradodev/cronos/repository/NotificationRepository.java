package com.coloradodev.cronos.repository;

import com.coloradodev.cronos.domain.Notification;
import com.coloradodev.cronos.domain.Notification.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByRecipientId(UUID recipientId);

    List<Notification> findByRecipientIdAndStatus(UUID recipientId, NotificationStatus status);

    List<Notification> findByStatus(NotificationStatus status);
}
