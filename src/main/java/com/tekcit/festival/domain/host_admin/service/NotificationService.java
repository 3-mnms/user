package com.tekcit.festival.domain.host_admin.service;

import com.tekcit.festival.domain.host_admin.dto.response.NotificationListDTO;
import com.tekcit.festival.domain.host_admin.dto.response.NotificationResponseDTO;
import com.tekcit.festival.domain.host_admin.entity.Notification;
import com.tekcit.festival.domain.host_admin.repository.NotificationRepository;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import com.tekcit.festival.domain.host_admin.dto.response.BookingInfoDTO;
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
    private final FcmService fcmService;

    // 사용자 알림 히스토리 조회
    public List<NotificationListDTO> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderBySentAtDesc(userId);
        return notifications.stream()
                .map(NotificationListDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 알림 단건 상세 조회
    public NotificationResponseDTO getNotificationDetail(Long nid, Long userId) {
        Notification notification = notificationRepository.findById(nid)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_RESOURCE);
        }

        return NotificationResponseDTO.fromEntity(notification);
    }

    /*@Transactional
    public void sendNotifications(List<BookingInfoDTO> bookingInfos) {
        log.info("총 {}명에게 정해진 시각에 알림 발송을 합니다.", bookingInfos.size());

        List<Long> userIds = bookingInfos.stream()
                .map(BookingInfoDTO::getUserId)
                .collect(Collectors.toList());

        if (!bookingInfos.isEmpty()) {
            BookingInfoDTO firstInfo = bookingInfos.get(0);
            // 제목에 fname을 추가하여 [공연명] 제목 형식으로 변경
            String finalTitle = String.format("[%s] %s", firstInfo.getFname(), firstInfo.getNotificationTitle());
            fcmService.sendMessageToUsers(userIds, finalTitle, firstInfo.getNotificationBody());
        }

        List<Notification> newNotifications = bookingInfos.stream()
                .map(info -> Notification.builder()
                        .userId(info.getUserId())
                        .title(info.getNotificationTitle())
                        .body(info.getNotificationBody())
                        .isRead(false)
                        .fname(info.getFname())
                        .build())
                .collect(Collectors.toList());

        notificationRepository.saveAll(newNotifications);

        log.info("알림 발송 확정 및 DB 저장이 완료되었습니다. {}건.", newNotifications.size());
    }*/
}