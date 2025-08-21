package com.tekcit.festival.domain.host_admin.dto.response;

import com.tekcit.festival.domain.host_admin.entity.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 알림 조회 시 클라이언트에게 반환할 데이터 전송 객체 (DTO)
 */
@Getter
@Setter
@Builder
public class NotificationResponseDTO {
    private Long id;
    private String title;
    private String body;
    private boolean isRead;
    private LocalDateTime sentAt;

    public static NotificationResponseDTO fromEntity(Notification notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .isRead(notification.isRead())
                .sentAt(notification.getSentAt())
                .build();
    }
}