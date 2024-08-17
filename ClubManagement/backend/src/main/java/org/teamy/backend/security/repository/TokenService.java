package org.teamy.backend.security.repository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.teamy.backend.security.model.Token;

public interface TokenService {
    UsernamePasswordAuthenticationToken readToken(String accessToken);
    Token createToken(UserDetails user);
    Token refresh(String accessToken, String refreshTokenId);
    void logout(String username);
}