package org.teamy.backend.security.repository;

import org.teamy.backend.security.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
    Optional<RefreshToken> get(String id);
    void save(RefreshToken token);
    void deleteAllForUsername(String username);
    void delete(String id);
}