package com.tekcit.festival.domain.user.dto.request;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationScheduleDTO {
    private Long festivalId;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 20, message = "제목을 20자 이내로 입력해 주세요.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 50, message = "내용을 50자 이내로 입력해 주세요.")
    private String body;

    private LocalDateTime sendTime;
}


