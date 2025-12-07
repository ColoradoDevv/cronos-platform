package com.coloradodev.cronos.exception;

/**
 * Exception thrown when attempting to create a duplicate resource.
 * HTTP 409
 */
public class DuplicateResourceException extends ConflictException {

    private final String field;
    private final String value;

    public DuplicateResourceException(String resourceType, String field, String value) {
        super(
                String.format("%s with %s '%s' already exists", resourceType, field, value),
                "DUPLICATE_RESOURCE");
        this.field = field;
        this.value = value;
        this.withDetail("field", field);
        this.withDetail("value", value);
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}
