package com.tekcit.festival.domain.host_admin.dto.response;

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
    @Schema(description = "알림 발송 시각", example = "2024-12-25T18:00:00")
    private LocalDateTime sentAt;
}