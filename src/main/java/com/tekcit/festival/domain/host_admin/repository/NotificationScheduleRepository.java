package com.tekcit.festival.domain.host_admin.repository;

import com.tekcit.festival.domain.host_admin.entity.NotificationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationScheduleRepository extends JpaRepository<NotificationSchedule, Long> {

    // 특정 발송 시각에 해당하는 모든 예약 알림 목록을 조회 (스케줄러에서 사용)
    List<NotificationSchedule> findBySendTime(LocalDateTime sendTime);

    // 스케줄러 로직 변경에 따라 추가된 메서드: 특정 시간 범위 내에 발송될 알림 목록 조회
    List<NotificationSchedule> findBySendTimeBetween(LocalDateTime start, LocalDateTime end);

    // 동일한 페스티벌 ID, 공연 시작 시각, 발송 시각을 가진 알림 예약이 존재하는지 중복을 확인
    boolean existsByFidAndStartAtAndSendTime(String fid, LocalDateTime startAt, LocalDateTime sendTime);

    // 특정 페스티벌의 특정 발송 시각(예: 하루) 내에 등록된 알림 예약의 개수를 세어 제한 (50개)
    long countByFidAndSendTimeBetween(String fid, LocalDateTime start, LocalDateTime end);

    // 특정 페스티벌에 대한 모든 알림 예약을 발송 시각 기준 최신순으로 조회
    List<NotificationSchedule> findAllByFidOrderBySendTimeDesc(String fid);

    // 아직 발송되지 않았고 발송 시각이 현재 시간보다 이전인 알림들을 조회, 제한
    List<NotificationSchedule> findByIsSentFalseAndSendTimeBefore(LocalDateTime cutoff);

    // 특정 사용자(주최자)가 등록한 모든 알림 예약 목록을 조회
    List<NotificationSchedule> findByUserId(Long userId);
}