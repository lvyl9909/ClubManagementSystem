package org.teamy.backend.model;

public enum EventStatus {
    Ongoing,
    Cancelled;

    public static EventStatus fromString(String status) {
        try {
            return EventStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
