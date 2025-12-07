package com.coloradodev.cronos.exception;

public class SlotNotAvailableException extends RuntimeException {

    public SlotNotAvailableException(String message) {
        super(message);
    }

    public SlotNotAvailableException() {
        super("The requested time slot is not available");
    }
}
