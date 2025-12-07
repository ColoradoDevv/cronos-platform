package com.coloradodev.cronos.exception;

/**
 * Exception thrown when attempting to create a user with a duplicate email.
 * HTTP 409
 */
public class DuplicateEmailException extends DuplicateResourceException {

    public DuplicateEmailException(String email) {
        super("User", "email", email);
    }
}
