package com.tekcit.festival.config.security.userdetails;

import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.enums.UserRole;
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
        // 일반 사용자일 경우
        if (user.getRole() == UserRole.USER && user.getUserProfile() != null) {
            return user.getUserProfile().isActive();
        }

        // 축제 주최측일 경우
        if (user.getRole() == UserRole.HOST && user.getHostProfile() != null) {
            return user.getHostProfile().isActive();
        }

        // 운영관리자는 항상 활성
        return true;
    }
}
