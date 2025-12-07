package com.coloradodev.cronos.service;

import com.coloradodev.cronos.domain.Booking;
import com.coloradodev.cronos.domain.Client;
import com.coloradodev.cronos.dto.client.ClientRequestDTO;
import com.coloradodev.cronos.exception.BusinessRuleException;
import com.coloradodev.cronos.exception.ResourceNotFoundException;
import com.coloradodev.cronos.repository.BookingRepository;
import com.coloradodev.cronos.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing clients within a tenant.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final BookingRepository bookingRepository;
    private final AuditService auditService;

    /**
     * Create a new client for a tenant.
     */
    @Transactional
    public Client createClient(UUID tenantId, ClientRequestDTO request) {
        // Check for duplicate email
        if (request.getEmail() != null && clientRepository.existsByTenantIdAndEmail(tenantId, request.getEmail())) {
            throw new BusinessRuleException("DUPLICATE_EMAIL",
                    "Client with email already exists: " + request.getEmail());
        }

        Client client = new Client();
        client.setTenantId(tenantId);
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setNotes(request.getNotes());

        Client saved = clientRepository.save(client);

        auditService.logCreate(tenantId, null, "Client", saved.getId(),
                Map.of("name", saved.getFirstName() + " " + saved.getLastName()));

        log.info("Created client {} {} for tenant {}", saved.getFirstName(), saved.getLastName(), tenantId);
        return saved;
    }

    /**
     * Update an existing client.
     */
    @Transactional
    public Client updateClient(UUID tenantId, UUID clientId, ClientRequestDTO request) {
        Client client = getClientById(tenantId, clientId);

        // Check for duplicate email if changed
        if (request.getEmail() != null && !request.getEmail().equals(client.getEmail())) {
            if (clientRepository.existsByTenantIdAndEmail(tenantId, request.getEmail())) {
                throw new BusinessRuleException("DUPLICATE_EMAIL",
                        "Client with email already exists: " + request.getEmail());
            }
        }

        Map<String, Object> oldValues = Map.of(
                "firstName", client.getFirstName(),
                "lastName", client.getLastName(),
                "email", client.getEmail() != null ? client.getEmail() : "",
                "phone", client.getPhone() != null ? client.getPhone() : "");

        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        if (request.getNotes() != null) {
            client.setNotes(request.getNotes());
        }

        Client saved = clientRepository.save(client);

        Map<String, Object> newValues = Map.of(
                "firstName", saved.getFirstName(),
                "lastName", saved.getLastName(),
                "email", saved.getEmail() != null ? saved.getEmail() : "",
                "phone", saved.getPhone() != null ? saved.getPhone() : "");

        auditService.logUpdate(tenantId, null, "Client", saved.getId(), oldValues, newValues);

        log.info("Updated client {} for tenant {}", clientId, tenantId);
        return saved;
    }

    /**
     * Get a client by ID.
     */
    @Transactional(readOnly = true)
    public Client getClientById(UUID tenantId, UUID clientId) {
        return clientRepository.findByTenantIdAndId(tenantId, clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", clientId.toString()));
    }

    /**
     * Get all clients for a tenant with pagination.
     */
    @Transactional(readOnly = true)
    public Page<Client> getClientsByTenant(UUID tenantId, Pageable pageable) {
        List<Client> clients = clientRepository.findByTenantId(tenantId);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), clients.size());

        if (start > clients.size()) {
            return new PageImpl<>(List.of(), pageable, clients.size());
        }

        return new PageImpl<>(clients.subList(start, end), pageable, clients.size());
    }

    /**
     * Search clients by name.
     */
    @Transactional(readOnly = true)
    public List<Client> searchClients(UUID tenantId, String query) {
        return clientRepository.searchByName(tenantId, query);
    }

    /**
     * Get client's booking history.
     */
    @Transactional(readOnly = true)
    public List<Booking> getClientAppointmentHistory(UUID tenantId, UUID clientId) {
        // Verify client exists
        getClientById(tenantId, clientId);
        return bookingRepository.findByTenantIdAndClientIdOrderByCreatedAtDesc(tenantId, clientId);
    }

    /**
     * Add a note to a client.
     */
    @Transactional
    public Client addClientNote(UUID tenantId, UUID clientId, String note) {
        Client client = getClientById(tenantId, clientId);

        String existingNotes = client.getNotes();
        String newNotes = existingNotes != null
                ? existingNotes + "\n---\n" + note
                : note;

        client.setNotes(newNotes);

        Client saved = clientRepository.save(client);

        auditService.logAction(tenantId, null, "ADD_NOTE", "Client", clientId,
                null, Map.of("note", note.length() > 50 ? note.substring(0, 50) + "..." : note));

        log.info("Added note to client {} for tenant {}", clientId, tenantId);
        return saved;
    }

    /**
     * Soft delete a client (mark as inactive).
     * Note: This implementation assumes a soft delete pattern.
     * You may want to add an isActive field to the Client entity.
     */
    @Transactional
    public void deleteClient(UUID tenantId, UUID clientId) {
        Client client = getClientById(tenantId, clientId);

        // For now, we'll actually delete. Add isActive field for soft delete.
        clientRepository.delete(client);

        auditService.logDelete(tenantId, null, "Client", clientId,
                Map.of("name", client.getFirstName() + " " + client.getLastName()));

        log.info("Deleted client {} for tenant {}", clientId, tenantId);
    }

    /**
     * Merge two client records (move all bookings from source to target, then
     * delete source).
     */
    @Transactional
    public Client mergeClients(UUID tenantId, UUID sourceClientId, UUID targetClientId) {
        Client sourceClient = getClientById(tenantId, sourceClientId);
        Client targetClient = getClientById(tenantId, targetClientId);

        if (sourceClientId.equals(targetClientId)) {
            throw new BusinessRuleException("SAME_CLIENT", "Cannot merge a client with itself");
        }

        // Get all bookings for source client and reassign to target
        List<Booking> sourceBookings = bookingRepository.findByTenantIdAndClientIdOrderByCreatedAtDesc(
                tenantId, sourceClientId);

        for (Booking booking : sourceBookings) {
            booking.setClientId(targetClientId);
            bookingRepository.save(booking);
        }

        // Merge notes if source has any
        if (sourceClient.getNotes() != null && !sourceClient.getNotes().isEmpty()) {
            String mergedNote = String.format("[Merged from %s %s]\n%s",
                    sourceClient.getFirstName(), sourceClient.getLastName(),
                    sourceClient.getNotes());
            addClientNote(tenantId, targetClientId, mergedNote);
        }

        // Delete source client
        clientRepository.delete(sourceClient);

        auditService.logAction(tenantId, null, "MERGE_CLIENTS", "Client", targetClientId,
                Map.of("sourceClientId", sourceClientId,
                        "sourceName", sourceClient.getFirstName() + " " + sourceClient.getLastName()),
                Map.of("bookingsMerged", sourceBookings.size()));

        log.info("Merged client {} into {} for tenant {}", sourceClientId, targetClientId, tenantId);
        return clientRepository.findById(targetClientId).orElse(targetClient);
    }

    /**
     * Find client by email.
     */
    @Transactional(readOnly = true)
    public Client findByEmail(UUID tenantId, String email) {
        return clientRepository.findByTenantIdAndEmail(tenantId, email).orElse(null);
    }

    /**
     * Find client by phone.
     */
    @Transactional(readOnly = true)
    public Client findByPhone(UUID tenantId, String phone) {
        return clientRepository.findByTenantIdAndPhone(tenantId, phone).orElse(null);
    }

    /**
     * Find or create a client based on email/phone.
     * Useful for booking flow where client may or may not exist.
     */
    @Transactional
    public Client findOrCreateClient(UUID tenantId, String firstName, String lastName,
            String email, String phone) {
        // Try to find by email first
        if (email != null) {
            Client existing = findByEmail(tenantId, email);
            if (existing != null) {
                return existing;
            }
        }

        // Try to find by phone
        if (phone != null) {
            Client existing = findByPhone(tenantId, phone);
            if (existing != null) {
                return existing;
            }
        }

        // Create new client
        ClientRequestDTO request = new ClientRequestDTO();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPhone(phone);

        return createClient(tenantId, request);
    }
}
