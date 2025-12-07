package com.coloradodev.cronos.dto.notification;

import com.coloradodev.cronos.domain.Notification.NotificationChannel;
import com.coloradodev.cronos.domain.Notification.NotificationStatus;
import com.coloradodev.cronos.domain.Notification.RecipientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for Notification entity.
 * Notifications are system-generated, so no request DTO needed.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private UUID id;
    private RecipientType recipientType;
    private UUID recipientId;
    private NotificationChannel channel;
    private String templateId;
    private String subject;
    private String body;
    private LocalDateTime sentAt;
    private NotificationStatus status;
    private LocalDateTime createdAt;
}
