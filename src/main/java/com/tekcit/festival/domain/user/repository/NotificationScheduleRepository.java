package com.tekcit.festival.domain.user.repository;

import com.tekcit.festival.domain.user.entity.Festival;
import com.tekcit.festival.domain.user.entity.NotificationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationScheduleRepository extends JpaRepository<NotificationSchedule, Long> {
    List<NotificationSchedule> findByIsSentFalseAndSendTimeBefore(LocalDateTime time);
    List<NotificationSchedule> findByFestival(Festival festival);
}
