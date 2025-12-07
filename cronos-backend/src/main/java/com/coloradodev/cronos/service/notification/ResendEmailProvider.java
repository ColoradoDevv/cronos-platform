package com.coloradodev.cronos.service.notification;

import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.domain.Staff;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Email provider implementation using Resend API.
 * Active in non-dev profiles (staging, prod).
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Profile("!dev")
public class ResendEmailProvider implements EmailNotificationProvider {

    @Value("${resend.api-key:}")
    private String apiKey;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name}")
    private String fromName;

    @Value("${app.base-url}")
    private String baseUrl;

    private final TemplateEngine templateEngine;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Async
    @Override
    public boolean sendBookingConfirmation(Booking booking) {
        try {
            Map<String, Object> data = buildBookingData(booking);
            data.put("bookingUrl", baseUrl + "/booking/" + booking.getId());

            String htmlContent = renderTemplate("booking-confirmation", data);
            String subject = "Confirmación de reserva - " + data.get("serviceName");

            return sendEmail(booking.getClientEmail(), subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to send booking confirmation for booking {}: {}",
                    booking.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Async
    @Override
    public boolean sendBookingCancellation(Booking booking, String reason) {
        try {
            Map<String, Object> data = buildBookingData(booking);
            data.put("cancellationReason", reason);

            String htmlContent = renderTemplate("booking-cancellation", data);
            String subject = "Cancelación de reserva";

            return sendEmail(booking.getClientEmail(), subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to send cancellation email for booking {}: {}",
                    booking.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Async
    @Override
    public boolean sendBookingReschedule(Booking booking, String oldTime, String newTime) {
        try {
            Map<String, Object> data = buildBookingData(booking);
            data.put("oldTime", oldTime);
            data.put("newTime", newTime);
            data.put("bookingUrl", baseUrl + "/booking/" + booking.getId());

            String htmlContent = renderTemplate("booking-reschedule", data);
            String subject = "Cambio de horario de tu reserva";

            return sendEmail(booking.getClientEmail(), subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to send reschedule email for booking {}: {}",
                    booking.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Async
    @Override
    public boolean sendAppointmentReminder(Booking booking, int hoursBeforeAppointment) {
        try {
            Map<String, Object> data = buildBookingData(booking);
            data.put("hoursUntil", hoursBeforeAppointment);
            data.put("bookingUrl", baseUrl + "/booking/" + booking.getId());

            String htmlContent = renderTemplate("appointment-reminder", data);
            String subject = "Recordatorio: Tu cita es mañana";

            return sendEmail(booking.getClientEmail(), subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to send reminder for booking {}: {}",
                    booking.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Async
    @Override
    public boolean sendStaffNotification(Staff staff, String subject, String message) {
        try {
            Map<String, Object> data = new HashMap<>();
            // Staff name comes from the associated User entity
            String staffName = staff.getUser() != null
                    ? staff.getUser().getFirstName() + " " + staff.getUser().getLastName()
                    : "Staff";
            data.put("staffName", staffName);
            data.put("message", message);

            String htmlContent = renderTemplate("staff-notification", data);

            String email = staff.getUser() != null ? staff.getUser().getEmail() : null;
            if (email == null) {
                log.warn("No email found for staff {}", staff.getId());
                return false;
            }

            return sendEmail(email, subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to send notification to staff {}: {}",
                    staff.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean sendEmail(String to, String subject, String htmlBody) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("Resend API key not configured, skipping email to: {}", to);
            return false;
        }

        try {
            Resend resend = new Resend(apiKey);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(String.format("%s <%s>", fromName, fromEmail))
                    .to(to)
                    .subject(subject)
                    .html(htmlBody)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            log.info("Email sent successfully to {} - ID: {}", to, response.getId());
            return true;

        } catch (ResendException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }

    private Map<String, Object> buildBookingData(Booking booking) {
        Map<String, Object> data = new HashMap<>();
        data.put("clientName", booking.getClientName());
        data.put("clientEmail", booking.getClientEmail());
        data.put("serviceName", booking.getService() != null ? booking.getService().getName() : "Service");
        data.put("startTime", booking.getStartTime());
        data.put("endTime", booking.getEndTime());
        data.put("date", booking.getStartTime().format(DATE_FORMATTER));
        data.put("time", booking.getStartTime().format(TIME_FORMATTER));
        data.put("duration", booking.getService() != null ? booking.getService().getDuration() : 60);

        if (booking.getStaff() != null && booking.getStaff().getUser() != null) {
            data.put("staffName", booking.getStaff().getUser().getFirstName() + " " +
                    booking.getStaff().getUser().getLastName());
        }

        if (booking.getTenant() != null) {
            data.put("tenantName", booking.getTenant().getName());
        }

        return data;
    }

    private String renderTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process("email/" + templateName, context);
    }
}
