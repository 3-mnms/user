package com.tekcit.festival.domain.user.repository;

import com.tekcit.festival.domain.user.entity.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalRepository extends JpaRepository<Festival, Long> {
}
