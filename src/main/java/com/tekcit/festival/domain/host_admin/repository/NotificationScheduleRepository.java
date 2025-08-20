package com.tekcit.festival.domain.host_admin.repository;

import com.tekcit.festival.domain.host_admin.entity.NotificationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationScheduleRepository extends JpaRepository<NotificationSchedule, Long> {
    // 중복 방지 사전 체크
    boolean existsByFidAndStartAtAndSendTime(String fid, LocalDateTime startAt, LocalDateTime sendTime);
    // 일일 건수 제한 체크
    long countByFidAndSendTimeBetween(String fid, LocalDateTime start, LocalDateTime end);
    // 페스티벌 목록 조회
    List<NotificationSchedule> findAllByFidOrderBySendTimeDesc(String fid);
    // 배치용
    List<NotificationSchedule> findByIsSentFalseAndSendTimeBefore(LocalDateTime cutoff);
    // 주최자 별 등록 알림 조회
    List<NotificationSchedule> findByUserId(Long userId);
}