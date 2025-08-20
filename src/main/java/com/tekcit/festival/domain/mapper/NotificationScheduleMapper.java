package com.tekcit.festival.domain.mapper;

import com.tekcit.festival.domain.host_admin.dto.response.NotificationScheduleResponseDTO;
import com.tekcit.festival.domain.host_admin.entity.NotificationSchedule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationScheduleMapper {

    public NotificationScheduleResponseDTO toDto(NotificationSchedule e) {
        return new NotificationScheduleResponseDTO(
                e.getScheduleId(),
                e.getTitle(),
                e.getBody(),
                e.getSendTime(),
                e.isSent(),
                e.getFid(),
                e.getStartAt()
        );
    }

    public List<NotificationScheduleResponseDTO> toDtoList(List<NotificationSchedule> list) {
        return list.stream().map(this::toDto).toList();
    }
}