package com.tekcit.festival.domain.user.repository;

import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.enums.OAuthProvider;
import com.tekcit.festival.domain.user.enums.UserRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<User> findAllUserByRole(UserRole userRole);

    @EntityGraph(attributePaths = {"hostProfile"})
    List<User> findAllHostByRole(UserRole userRole);

    @Query("select u from User u join fetch u.userProfile where u.email = :email")
    Optional<User> findByEmailWithProfile(@Param("email") String email);
}
