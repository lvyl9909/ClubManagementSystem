package org.teamy.backend.model.request;


import org.teamy.backend.model.ResponseEntity;

@FunctionalInterface
public interface RequestHandler {
     ResponseEntity handle();
}