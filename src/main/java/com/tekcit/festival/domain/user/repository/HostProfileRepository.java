package com.tekcit.festival.domain.user.repository;

import com.tekcit.festival.domain.user.entity.HostProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HostProfileRepository extends JpaRepository<HostProfile, Long> {
    Optional<HostProfile> findByUser_UserId(Long userId);
}
