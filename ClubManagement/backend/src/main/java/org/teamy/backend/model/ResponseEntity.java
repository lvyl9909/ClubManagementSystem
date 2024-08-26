package org.teamy.backend.model;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public class ResponseEntity {
    private final int status;
    private final Object body;

    public ResponseEntity(int status, Object body) {
        this.status = status;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public Optional<Object> getBody() {
        return Optional.ofNullable(body);
    }
    public static ResponseEntity of(int status, Object body) {
        return new ResponseEntity(status, body);
    }

    public static ResponseEntity ok(Object body) {
        return new ResponseEntity(HttpServletResponse.SC_OK, body);
    }
    public static ResponseEntity create(Object body) {
        return new ResponseEntity(HttpServletResponse.SC_CREATED, body);
    }

}
