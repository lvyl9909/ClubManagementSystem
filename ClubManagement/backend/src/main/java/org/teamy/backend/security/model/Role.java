package org.teamy.backend.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class Role implements GrantedAuthority {
    private String roleName;

    public Role(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String getAuthority() {
        return String.format("ROLE_%s", roleName);
    }

    // Getter 和 Setter 方法...
}