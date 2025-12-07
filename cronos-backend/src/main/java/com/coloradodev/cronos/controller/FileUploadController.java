package com.coloradodev.cronos.controller;

import com.coloradodev.cronos.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * REST Controller for file uploads.
 * Handles tenant logos, service images, and staff photos.
 */
@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class FileUploadController {

    private final StorageService storageService;

    /**
     * Upload a tenant logo.
     */
    @PostMapping("/logo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadLogo(
            @RequestParam("file") MultipartFile file) {
        String key = storageService.upload(file, "logos");
        String url = storageService.getUrl(key);
        return ResponseEntity.ok(Map.of("key", key, "url", url));
    }

    /**
     * Upload a service image.
     */
    @PostMapping("/service-image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadServiceImage(
            @RequestParam("file") MultipartFile file) {
        String key = storageService.upload(file, "services");
        String url = storageService.getUrl(key);
        return ResponseEntity.ok(Map.of("key", key, "url", url));
    }

    /**
     * Upload a staff photo.
     */
    @PostMapping("/staff-photo")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Map<String, String>> uploadStaffPhoto(
            @RequestParam("file") MultipartFile file) {
        String key = storageService.upload(file, "staff");
        String url = storageService.getUrl(key);
        return ResponseEntity.ok(Map.of("key", key, "url", url));
    }

    /**
     * Delete a file.
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFile(@RequestParam("key") String key) {
        storageService.delete(key);
        return ResponseEntity.noContent().build();
    }
}
