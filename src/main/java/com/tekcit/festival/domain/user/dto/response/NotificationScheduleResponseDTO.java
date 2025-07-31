package com.tekcit.festival.domain.user.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationScheduleResponseDTO {
    private Long scheduleId;
    private String title;
    private String body;
    private LocalDateTime sendTime;
    private Long festivalId;
    private String festivalTitle;
}