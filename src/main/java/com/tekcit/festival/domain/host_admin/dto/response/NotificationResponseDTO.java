package com.tekcit.festival.domain.host_admin.dto.response;

import com.tekcit.festival.domain.host_admin.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Schema(description = "알림 상세 조회 응답 DTO")
public class NotificationResponseDTO {
    @Schema(description = "알림 ID", example = "1")
    private Long nid;
    @Schema(description = "알림 제목", example = "공연 시작 10분 전!")
    private String title;
    @Schema(description = "알림 내용", example = "지금 바로 입장 준비를 해주세요.")
    private String body;
    @Schema(description = "읽음 여부", example = "false")
    private boolean isRead;
    @Schema(description = "알림 발송 시각", example = "2024-12-25T18:00:00")
    private LocalDateTime sentAt;

    public static NotificationResponseDTO fromEntity(Notification notification) {
        return NotificationResponseDTO.builder()
                .nid(notification.getNid())
                .title(notification.getTitle())
                .body(notification.getBody())
                .isRead(notification.isRead())
                .sentAt(notification.getSentAt())
                .build();
    }
}