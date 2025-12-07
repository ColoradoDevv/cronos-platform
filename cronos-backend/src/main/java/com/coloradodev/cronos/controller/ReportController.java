package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.core.tenant.TenantContext;
import com.coloradodev.cronos.service.ReportingService;
import com.coloradodev.cronos.service.ReportingService.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * REST Controller for business reports and analytics.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportingService reportingService;

    /**
     * Get appointment statistics for a date range.
     */
    @GetMapping("/appointments")
    public ResponseEntity<AppointmentStats> getAppointmentStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        AppointmentStats stats = reportingService.getAppointmentStats(tenantId, start, end);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get revenue report for a date range.
     */
    @GetMapping("/revenue")
    public ResponseEntity<RevenueReport> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        RevenueReport report = reportingService.getRevenueReport(tenantId, start, end);
        return ResponseEntity.ok(report);
    }

    /**
     * Get client statistics.
     */
    @GetMapping("/clients")
    public ResponseEntity<ClientStats> getClientStats() {
        UUID tenantId = TenantContext.getCurrentTenantId();
        ClientStats stats = reportingService.getClientStats(tenantId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get staff performance metrics.
     */
    @GetMapping("/staff/{id}")
    public ResponseEntity<StaffPerformance> getStaffPerformance(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        StaffPerformance performance = reportingService.getStaffPerformance(tenantId, id, start, end);
        return ResponseEntity.ok(performance);
    }

    /**
     * Get no-show report for a date range.
     */
    @GetMapping("/no-shows")
    public ResponseEntity<NoShowReport> getNoShowReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        NoShowReport report = reportingService.getNoShowReport(tenantId, start, end);
        return ResponseEntity.ok(report);
    }

    /**
     * Get cancellation report for a date range.
     */
    @GetMapping("/cancellations")
    public ResponseEntity<CancellationReport> getCancellationReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        CancellationReport report = reportingService.getCancellationReport(tenantId, start, end);
        return ResponseEntity.ok(report);
    }
}
