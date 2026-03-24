package com.hyundai.DMS.security;

import com.hyundai.DMS.domain.entity.User;
import com.hyundai.DMS.domain.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class DmsUserDetails implements UserDetails {

    private final Long userId;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private final Long dealershipId;
    private final boolean enabled;

    public DmsUserDetails(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.role = user.getRole();
        this.dealershipId = user.getDealership() != null ? user.getDealership().getId() : null;
        this.enabled = user.getStatus() != null && user.getStatus().getValue().equals("active");
    }

    public boolean isSuperAdmin() { return role == Role.SUPER_ADMIN; }
    public boolean isAdmin() { return role == Role.ADMIN || role == Role.SUPER_ADMIN; }
    public boolean isManagerOrAbove() { return role == Role.MANAGER || isAdmin(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String getPassword() { return passwordHash; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}
