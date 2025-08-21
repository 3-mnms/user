package com.tekcit.festival.domain.host_admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationScheduleDTO {

    @NotBlank(message = "페스티벌 ID는 필수입니다.")
    private String fid;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 20, message = "제목은 20자 이내여야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 50, message = "내용은 50자 이내여야 합니다.")
    private String body;

    @NotNull(message = "공연 시작 시간은 필수입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startAt;

    @NotNull(message = "발송 시간은 필수입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sendTime;
}

