package com.tekcit.festival.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleSimpleDTO {
    private Long id;
    private String title;
    private String body;
    private LocalDateTime sendTime;
    private Long festivalId;
}
