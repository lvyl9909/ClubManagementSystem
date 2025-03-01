package org.teamy.backend.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.teamy.backend.security.model.Role;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Person implements UserDetails {
    private Long id;
    private String username;
    private String name;
    private String email;
    private Long phoneNumber;
    private String password;
    private boolean isActive;
    private Set<Role> roles = new HashSet<>();
    public Person() {
    }

    public Person(Long id, String username, String name, String email, Long phoneNumber, String password, boolean isActive, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.isActive = isActive;
        this.roles = roles;
    }

    public Person(String name, String email, Long phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    public Person(String username, String password, String email, boolean isActive, Set<Role> roles) {
        this.name = username;
        this.password = password;
        this.email = email;
        this.isActive = isActive;
        this.roles = roles;
    }
    public Person(String name, String email, Long phoneNumber,String password) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password=password;
    }
    public Person(String name, String email, Long phoneNumber,String password,String username) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password=password;
        this.username=username;
        this.roles.add(new Role("USER")); // add USER role by default
        this.isActive=true;
    }
    public Person(Long id,String name, String email, Long phoneNumber,String password,String username,boolean isActive,String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password=password;
        this.username=username;
        this.roles.add(new Role(role)); // 默认添加 USER 角色
        this.isActive=isActive;
    }

    public Person(Long id,String name, String email, Long phoneNumber,String username,boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username=username;
        this.roles.add(new Role("USER")); //Add USER role by default
        this.isActive=isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPassword() {
        return password;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>(roles);  // 直接返回 roles 作为权限
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
