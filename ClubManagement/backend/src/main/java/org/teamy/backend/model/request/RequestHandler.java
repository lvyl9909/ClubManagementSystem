package org.teamy.backend.model.request;


@FunctionalInterface
public interface RequestHandler {
     ResponseEntity handle();
}