package com.tekcit.festival.domain.user.dto.request;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationScheduleDTO {
    private Long festivalId;
    private String title;
    private String body;
    private LocalDateTime sendTime;
}

