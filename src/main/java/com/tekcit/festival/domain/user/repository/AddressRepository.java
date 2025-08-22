package com.tekcit.festival.domain.user.repository;

import com.tekcit.festival.domain.user.entity.Address;
import com.tekcit.festival.domain.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUserProfile(UserProfile userProfile);

    @Query("SELECT a FROM Address a WHERE a.userProfile.user.userId = :userId")
    List<Address> findAllByUserId(Long userId);

    @Query("SELECT a FROM Address a WHERE a.userProfile.user.userId = :userId AND a.isDefault = true")
    Optional<Address> findDefaultByUserId(Long userId);
}
