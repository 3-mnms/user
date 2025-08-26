package com.tekcit.festival.domain.host_admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "알림 예약 수정 요청 DTO")
public class NotificationUpdateScheduleDTO {
    @NotBlank
    @Schema(description = "알림 제목", example = "공연 시작 30분 전!")
    private String title;

    @NotBlank
    @Schema(description = "알림 내용", example = "잠시 후 공연이 시작됩니다. 준비해 주세요.")
    private String body;

    @NotNull
    @Schema(description = "알림 발송 시각", example = "2024-12-25T18:00")
    private LocalDateTime sendTime;
}
