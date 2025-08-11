package com.tekcit.festival.domain.user.repository;

import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.enums.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByOauthProviderAndOauthProviderId(OAuthProvider provider, String providerUserId);

    Optional<User> findByEmail(String email);
}
