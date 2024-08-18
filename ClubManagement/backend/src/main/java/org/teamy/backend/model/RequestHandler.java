package org.teamy.backend.model;


@FunctionalInterface
public interface RequestHandler {
     ResponseEntity handle();
}