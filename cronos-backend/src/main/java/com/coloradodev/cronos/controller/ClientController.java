package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.core.tenant.TenantContext;
import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.domain.Client;
import com.coloradodev.cronos.dto.booking.BookingResponseDTO;
import com.coloradodev.cronos.dto.client.ClientNoteDTO;
import com.coloradodev.cronos.dto.client.ClientRequestDTO;
import com.coloradodev.cronos.dto.client.ClientResponseDTO;
import com.coloradodev.cronos.dto.mapper.BookingMapper;
import com.coloradodev.cronos.dto.mapper.ClientMapper;
import com.coloradodev.cronos.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing clients.
 */
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;
    private final BookingMapper bookingMapper;

    // ==================== Client CRUD ====================

    /**
     * Create a new client.
     */
    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(
            @Valid @RequestBody ClientRequestDTO request) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Client client = clientService.createClient(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientMapper.toResponseDTO(client));
    }

    /**
     * Get all clients for the current tenant with pagination.
     */
    @GetMapping
    public ResponseEntity<Page<ClientResponseDTO>> getClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        Page<Client> clients = clientService.getClientsByTenant(tenantId, pageable);
        Page<ClientResponseDTO> response = clients.map(clientMapper::toResponseDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Search clients by name or email.
     */
    @GetMapping("/search")
    public ResponseEntity<List<ClientResponseDTO>> searchClients(
            @RequestParam String q) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        List<Client> clients = clientService.searchClients(tenantId, q);
        List<ClientResponseDTO> response = clients.stream()
                .map(clientMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Get a client by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClient(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Client client = clientService.getClientById(tenantId, id);
        return ResponseEntity.ok(clientMapper.toResponseDTO(client));
    }

    /**
     * Update a client.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(
            @PathVariable UUID id,
            @Valid @RequestBody ClientRequestDTO request) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Client client = clientService.updateClient(tenantId, id, request);
        return ResponseEntity.ok(clientMapper.toResponseDTO(client));
    }

    /**
     * Delete (soft-delete) a client.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        clientService.deleteClient(tenantId, id);
    }

    // ==================== Client Appointments ====================

    /**
     * Get a client's appointment/booking history.
     */
    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<BookingResponseDTO>> getClientAppointments(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        List<Booking> bookings = clientService.getClientAppointmentHistory(tenantId, id);
        List<BookingResponseDTO> response = bookings.stream()
                .map(bookingMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    // ==================== Client Notes ====================

    /**
     * Add a note to a client.
     */
    @PostMapping("/{id}/notes")
    public ResponseEntity<ClientResponseDTO> addClientNote(
            @PathVariable UUID id,
            @Valid @RequestBody ClientNoteDTO noteRequest) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        Client client = clientService.addClientNote(tenantId, id, noteRequest.getNote());
        return ResponseEntity.ok(clientMapper.toResponseDTO(client));
    }
}
