package org.teamy.backend.model.exception;

public class NotEnoughTicketsException extends RuntimeException {
    public NotEnoughTicketsException(String message) {
        super(message);
    }
}