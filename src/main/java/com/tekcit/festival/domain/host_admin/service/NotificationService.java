package com.tekcit.festival.domain.host_admin.service;

import com.tekcit.festival.domain.host_admin.dto.response.NotificationResponseDTO;
import com.tekcit.festival.domain.host_admin.entity.Notification;
import com.tekcit.festival.domain.host_admin.repository.NotificationRepository;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponseDTO> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderBySentAtDesc(userId);
        return notifications.stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotificationResponseDTO getNotificationDetail(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found."));

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_RESOURCE, "Access denied.");
        }

        return NotificationResponseDTO.fromEntity(notification);
    }

    public void sendNotifications(List<BookingInfoDTO> bookingInfos) {
        log.info("🔔 총 {}명에게 알림 발송을 시작합니다.", bookingInfos.size());

        bookingInfos.forEach(info -> {
            log.info("➡️ 사용자 ID: {}에게 알림 발송 (공연 ID: {})", info.getUserId(), info.getFestivalId());
            // TODO: 실제 알림 발송 로직 구현 (예: Firebase Cloud Messaging 등)
            // String title = info.getNotificationTitle();
            // String body = info.getNotificationBody();
            // ...
        });

        log.info("✅ 알림 발송이 완료되었습니다.");
    }
}