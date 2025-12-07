package com.coloradodev.cronos.domain.embedded;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Client preferences for communication and notifications.
 * Embedded in Client entity for type-safe JSON storage.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientPreferences {

    /**
     * Preferred communication channel: EMAIL, SMS, or BOTH
     */
    private String communicationChannel;

    /**
     * Whether reminders are enabled for this client
     */
    private Boolean reminderEnabled;

    /**
     * Hours before appointment to send reminder (default: 24)
     */
    private Integer reminderHours;

    /**
     * Preferred language code (e.g., "en", "es")
     */
    private String language;

    /**
     * Client's timezone (e.g., "America/New_York")
     */
    private String timezone;

    /**
     * Additional notes or special requirements
     */
    private String notes;

    /**
     * Create default preferences
     */
    public static ClientPreferences createDefault() {
        ClientPreferences prefs = new ClientPreferences();
        prefs.setCommunicationChannel("EMAIL");
        prefs.setReminderEnabled(true);
        prefs.setReminderHours(24);
        prefs.setLanguage("en");
        prefs.setTimezone("America/New_York");
        return prefs;
    }
}
