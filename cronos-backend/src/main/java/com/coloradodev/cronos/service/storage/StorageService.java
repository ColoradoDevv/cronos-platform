package com.coloradodev.cronos.service.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Storage service abstraction for file uploads.
 * Implementations: LocalStorageService (dev), SupabaseStorageService (prod)
 */
public interface StorageService {

    /**
     * Upload a file to storage.
     *
     * @param file   the file to upload
     * @param folder the folder/prefix to store the file in
     * @return the storage key/path
     */
    String upload(MultipartFile file, String folder);

    /**
     * Delete a file from storage.
     *
     * @param key the storage key/path
     */
    void delete(String key);

    /**
     * Get the public URL for a stored file.
     *
     * @param key the storage key/path
     * @return the public URL
     */
    String getUrl(String key);
}
