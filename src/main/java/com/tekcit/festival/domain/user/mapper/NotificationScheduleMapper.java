package com.tekcit.festival.domain.user.mapper;

import com.tekcit.festival.domain.user.dto.response.NotificationScheduleResponseDTO;
import com.tekcit.festival.domain.user.dto.response.NotificationScheduleSummaryDTO;
import com.tekcit.festival.domain.user.entity.NotificationSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationScheduleMapper {

    @Mapping(source = "festival.id", target = "festivalId")
    @Mapping(source = "festival.title", target = "festivalTitle")
    NotificationScheduleResponseDTO toDto(NotificationSchedule schedule);

    List<NotificationScheduleResponseDTO> toDtoList(List<NotificationSchedule> schedules);

    NotificationScheduleSummaryDTO toSummaryDto(NotificationSchedule schedule);

    List<NotificationScheduleSummaryDTO> toSummaryDtoList(List<NotificationSchedule> schedules);
}
