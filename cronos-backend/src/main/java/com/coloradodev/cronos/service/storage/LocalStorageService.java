package com.coloradodev.cronos.service.storage;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Local filesystem storage implementation.
 * Used for development and Railway deployments with volumes.
 */
@Service
@Slf4j
public class LocalStorageService implements StorageService {

    @Value("${app.storage.path:/app/uploads}")
    private String uploadPath;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp", "image/gif");

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created upload directory: {}", uploadPath);
            }
        } catch (IOException e) {
            log.error("Could not create upload directory: {}", uploadPath, e);
        }
    }

    @Override
    public String upload(MultipartFile file, String folder) {
        validateFile(file);

        String filename = generateFilename(file.getOriginalFilename());
        String key = folder + "/" + filename;
        Path targetLocation = Paths.get(uploadPath).resolve(key);

        try {
            Files.createDirectories(targetLocation.getParent());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded successfully: {}", key);
            return key;
        } catch (IOException e) {
            log.error("Failed to store file: {}", filename, e);
            throw new RuntimeException("Failed to store file: " + filename, e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            Path path = Paths.get(uploadPath).resolve(key);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("File deleted: {}", key);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", key, e);
        }
    }

    @Override
    public String getUrl(String key) {
        return baseUrl + "/uploads/" + key;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File too large. Maximum size is 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Invalid file type. Allowed types: JPEG, PNG, WebP, GIF");
        }
    }

    private String generateFilename(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}
