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
}