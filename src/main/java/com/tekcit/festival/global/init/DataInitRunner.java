package com.tekcit.festival.global.init;

import com.tekcit.festival.domain.user.dto.request.HostProfileDTO;
import com.tekcit.festival.domain.user.dto.request.SignupUserDTO;
import com.tekcit.festival.domain.user.dto.request.UserProfileDTO;
import com.tekcit.festival.domain.user.entity.HostProfile;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.enums.GeocodeStatus;
import com.tekcit.festival.domain.user.enums.OAuthProvider;
import com.tekcit.festival.domain.user.enums.UserRole;
import com.tekcit.festival.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile({"dev"})
@RequiredArgsConstructor
public class DataInitRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        insertAdmin();
        insertHost();
        insertUser();
    }

    private boolean existsUser(Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE user_id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    private void insertAdmin() {
        if (existsUser(1L)) return;

        jdbcTemplate.update("""
            INSERT INTO users
            (user_id, login_id, login_pw, name, phone, email, role, oauth_provider, oauth_provider_id, is_email_verified, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, 'ADMIN', 'LOCAL', NULL, TRUE, NOW(), NOW())
        """, 1L, "adminId", passwordEncoder.encode("adminPassword"),
                "관리자", "010-1234-5678", "admin@example.com");
    }

    private void insertHost() {
        if (existsUser(2L)) return;

        jdbcTemplate.update("""
            INSERT INTO users
            (user_id, login_id, login_pw, name, phone, email, role, oauth_provider, oauth_provider_id, is_email_verified, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, 'HOST', 'LOCAL', NULL, TRUE, NOW(), NOW())
        """, 2L, "host1", passwordEncoder.encode("hostPassword"),
                "호스트1", "010-1111-2222", "host1@example.com");

        jdbcTemplate.update("""
            INSERT INTO host_profiles (b_name, is_active, user_id)
            VALUES (?, TRUE, ?)
        """, "테킷 엔터프라이즈", 2L);
    }

    private void insertUser() {
        if (existsUser(3L)) return;

        jdbcTemplate.update("""
            INSERT INTO users
            (user_id, login_id, login_pw, name, phone, email, role, oauth_provider, oauth_provider_id, is_email_verified, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, 'USER', 'LOCAL', NULL, TRUE, NOW(), NOW())
        """, 3L, "user1", passwordEncoder.encode("userPassword"),
                "홍길동", "010-3333-4444", "user1@example.com");

        jdbcTemplate.update("""
            INSERT INTO user_profiles (resident_num, age, gender, birth, is_active, user_id)
            VALUES (?, ?, ?, ?, TRUE, ?)
        """, "990101-1", 27, "MALE", "1999-01-01", 3L);

        Long profileId = jdbcTemplate.queryForObject(
                "SELECT u_id FROM user_profiles WHERE user_id = ?",
                Long.class, 3L);

        jdbcTemplate.update("""
            INSERT INTO addresses (address, zip_code, name, phone, is_default, u_id, is_geocoded)
            VALUES (?, ?, ?, ?, TRUE, ?, "PENDING")
        """, "서울 강남구 봉은사로81길 8", "06086", "홍길동", "010-3333-4444", profileId);
    }
}