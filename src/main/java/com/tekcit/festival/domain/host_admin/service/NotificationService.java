package com.tekcit.festival.domain.host_admin.service;

import com.tekcit.festival.domain.host_admin.dto.response.NotificationResponseDTO;
import com.tekcit.festival.domain.host_admin.entity.Notification;
import com.tekcit.festival.domain.host_admin.repository.NotificationRepository;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import com.tekcit.festival.domain.host_admin.dto.response.BookingInfoDTO; // Updated import
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
    private final FcmService fcmService; // FcmService 의존성 추가

    public List<NotificationResponseDTO> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderBySentAtDesc(userId);
        return notifications.stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public NotificationResponseDTO getNotificationDetail(Long nid, Long userId) {
        Notification notification = notificationRepository.findById(nid)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found."));

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_RESOURCE, "Access denied.");
        }

        return NotificationResponseDTO.fromEntity(notification);
    }

    @Transactional
    public void sendNotifications(List<BookingInfoDTO> bookingInfos) {
        log.info("🔔 총 {}명에게 알림 발송을 시작합니다.", bookingInfos.size());

        List<Long> userIds = bookingInfos.stream()
                .map(BookingInfoDTO::getUserId)
                .collect(Collectors.toList());

        // FCM 서비스 호출
        // bookingInfos 리스트에서 첫 번째 정보만 사용하여 제목과 본문을 가져옴 (모두 동일하다고 가정)
        if (!bookingInfos.isEmpty()) {
            BookingInfoDTO firstInfo = bookingInfos.get(0);
            fcmService.sendMessageToUsers(userIds, firstInfo.getNotificationTitle(), firstInfo.getNotificationBody());
        }

        // 알림 기록을 DB에 저장
        List<Notification> newNotifications = bookingInfos.stream()
                .map(info -> Notification.builder()
                        .userId(info.getUserId())
                        .title(info.getNotificationTitle())
                        .body(info.getNotificationBody())
                        .isRead(false)
                        .build())
                .collect(Collectors.toList());

        notificationRepository.saveAll(newNotifications);

        log.info("✅ 알림 발송 및 DB 저장이 완료되었습니다. {}건.", newNotifications.size());
    }
}