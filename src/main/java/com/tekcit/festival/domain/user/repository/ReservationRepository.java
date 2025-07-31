package com.tekcit.festival.domain.user.repository;

import com.tekcit.festival.domain.user.entity.Festival;
import com.tekcit.festival.domain.user.entity.Reservation;
import com.tekcit.festival.domain.user.entity.NotificationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByFestival(Festival festival);
}
