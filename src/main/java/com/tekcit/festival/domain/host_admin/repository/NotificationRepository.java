package com.tekcit.festival.domain.host_admin.repository;

import com.tekcit.festival.domain.host_admin.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 사용자의 알림 목록을 최신순(보낸 날짜 기준)으로 정렬하여 조회
    List<Notification> findByUserIdOrderBySentAtDesc(Long userId);
}