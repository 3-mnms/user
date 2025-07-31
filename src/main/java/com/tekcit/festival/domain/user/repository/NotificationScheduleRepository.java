package com.tekcit.festival.domain.user.repository;

import com.tekcit.festival.domain.user.entity.Festival;
import com.tekcit.festival.domain.user.entity.NotificationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationScheduleRepository extends JpaRepository<NotificationSchedule, Long> {

    // 하루 50건 제한을 위한 메서드
    long countByFestivalAndSendTimeBetween(Festival festival, LocalDateTime start, LocalDateTime end);

    // FCM 예약 발송 스케줄러
    List<NotificationSchedule> findByIsSentFalseAndSendTimeBefore(LocalDateTime time);

    // 공연별 예약 스케줄 조회 (관리자/주최자 용도)
    List<NotificationSchedule> findByFestival(Festival festival);
}
