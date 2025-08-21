package com.tekcit.festival.domain.user.repository;

import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.enums.OAuthProvider;
import com.tekcit.festival.domain.user.enums.UserRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByOauthProviderAndOauthProviderId(OAuthProvider provider, String providerUserId);

    Optional<User> findByEmail(String email);

    Optional<User> findByNameAndEmail(String name, String email);

    Optional<User> findByLoginIdAndName(String loginId, String name);

    @EntityGraph(attributePaths = {"userProfile", "userProfile.addresses"})
    List<User> findAllByRole(UserRole userRole);
}
