package org.teamy.backend.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    ADMIN,
    USER;

    public GrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority(String.format("ROLE_%s", name()));
    }
}
