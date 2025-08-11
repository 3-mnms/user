package com.tekcit.festival.domain.user.repository;

import com.tekcit.festival.domain.user.entity.Address;
import com.tekcit.festival.domain.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUserProfile(UserProfile userProfile);
}
