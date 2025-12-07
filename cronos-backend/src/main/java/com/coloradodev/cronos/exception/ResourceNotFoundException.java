package com.coloradodev.cronos.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 * HTTP 404
 */
public class ResourceNotFoundException extends CronosException {

    private final String resourceType;
    private final String resourceId;

    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(
                String.format("%s not found with id: %s", resourceType, resourceId),
                "RESOURCE_NOT_FOUND",
                HttpStatus.NOT_FOUND);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.withDetail("resourceType", resourceType);
        this.withDetail("resourceId", resourceId);
    }

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
        this.resourceType = "Resource";
        this.resourceId = "unknown";
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }
}
