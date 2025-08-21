package com.tekcit.festival.domain.host_admin.repository;

import com.tekcit.festival.domain.host_admin.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    //특정 사용자의 알림을 최신순으로 조회
    List<Notification> findByUserIdOrderBySentAtDesc(Long userId);
}