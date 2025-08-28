package com.tekcit.festival.domain.host_admin.dto.response;

import com.tekcit.festival.domain.host_admin.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "사용자 알림 응답 DTO")
public class NotificationResponseDTO {

    @Schema(description = "알림 ID", example = "1")
    private Long nid;

    @Schema(description = "알림 제목", example = "공연 시작 30분 전!")
    private String title;

    @Schema(description = "알림 내용", example = "잠시 후 공연이 시작됩니다. 준비해 주세요.")
    private String body;

    @Schema(description = "알림 발송 시각", example = "2024-12-25T18:00")
    private LocalDateTime sentAt;

    @Schema(description = "확인 여부", example = "false")
    private Boolean isRead;

    //@Schema(description = "페스티벌 고유 ID", example = "FB123456")
    //private String fid;

    @Schema(description = "공연명(스냅샷)", example = "뮤지컬 캣츠")
    private String fname;

    public static NotificationResponseDTO fromEntity(Notification notification) {
        return NotificationResponseDTO.builder()
                .nid(notification.getNid())
                .title(notification.getTitle())
                .body(notification.getBody())
                .sentAt(notification.getSentAt())
                .isRead(notification.isRead())
                //.fid(notification.getFid())
                .fname(notification.getFname())
                .build();
    }
}