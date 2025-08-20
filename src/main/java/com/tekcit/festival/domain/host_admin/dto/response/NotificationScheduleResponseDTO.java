package com.tekcit.festival.domain.host_admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "알림 예약 응답 DTO")
public class NotificationScheduleResponseDTO {

    @Schema(description = "예약 ID", example = "1")
    private Long scheduleId;

    @Schema(description = "알림 제목", example = "공연 시작 30분 전!")
    private String title;

    @Schema(description = "알림 내용", example = "잠시 후 공연이 시작됩니다. 준비해 주세요.")
    private String body;

    @Schema(description = "알림 발송 시각", example = "2024-12-25T18:00:00")
    private LocalDateTime sendTime;

    @Schema(description = "알림 발송 여부", example = "false")
    private Boolean sent;

    @Schema(description = "페스티벌 고유 ID", example = "FB123456")
    private String fid;

    @Schema(description = "공연 시작 시각", example = "2024-12-25T18:30:00")
    private LocalDateTime startAt;
}