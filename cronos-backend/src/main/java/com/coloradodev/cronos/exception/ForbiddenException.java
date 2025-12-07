package com.coloradodev.cronos.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when access to a resource is forbidden.
 * HTTP 403
 */
public class ForbiddenException extends CronosException {

    public ForbiddenException(String message) {
        super(message, "ACCESS_DENIED", HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.FORBIDDEN);
    }

    public static ForbiddenException tenantAccessDenied() {
        return new ForbiddenException(
                "Access to other tenant's data is not allowed",
                "TENANT_ACCESS_DENIED");
    }

    public static ForbiddenException insufficientPermissions() {
        return new ForbiddenException(
                "You don't have permission to perform this action",
                "INSUFFICIENT_PERMISSIONS");
    }

    public static ForbiddenException resourceOwnershipRequired() {
        return new ForbiddenException(
                "You can only access your own resources",
                "OWNERSHIP_REQUIRED");
    }
}
