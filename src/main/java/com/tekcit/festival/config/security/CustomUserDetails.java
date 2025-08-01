package com.tekcit.festival.config.security;

import com.tekcit.festival.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // "ROLE_USER"
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getLoginPw();
    }

    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; //만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠김 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 활성화 여부
    }
}
