package com.tekcit.festival.domain.host_admin.dto.response;

import com.tekcit.festival.domain.host_admin.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "알림 목록 조회 응답 DTO")
public class NotificationListDTO {
    @Schema(description = "알림 ID", example = "1")
    private Long nid;

    @Schema(description = "알림 제목", example = "공연 시작 10분 전!")
    private String title;

    @Schema(description = "알림 발송 시각", example = "2024-12-25T18:00:00")
    private LocalDateTime sentAt;

    @Schema(description = "공연명(스냅샷)", example = "뮤지컬 캣츠")
    private String fname;

    @Schema(description = "확인 여부", example = "false")
    private boolean isRead;

    public static NotificationListDTO fromEntity(Notification notification) {
        return new NotificationListDTO(
                notification.getNid(),
                notification.getTitle(),
                notification.getSentAt(),
                notification.getFname(),
                notification.isRead()
        );
    }
}