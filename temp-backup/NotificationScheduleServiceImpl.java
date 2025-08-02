package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.domain.exception.global.BusinessException;
import com.tekcit.festival.domain.exception.global.ErrorCode;
import com.tekcit.festival.domain.user.dto.request.NotificationScheduleDTO;
import com.tekcit.festival.domain.user.dto.response.NotificationScheduleResponseDTO;
import com.tekcit.festival.domain.user.dto.response.NotificationScheduleSummaryDTO;
import com.tekcit.festival.domain.user.entity.Festival;
import com.tekcit.festival.domain.user.entity.NotificationSchedule;
import com.tekcit.festival.domain.user.mapper.NotificationScheduleMapper;
import com.tekcit.festival.domain.user.repository.FestivalRepository;
import com.tekcit.festival.domain.user.repository.NotificationScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationScheduleServiceImpl implements NotificationScheduleService {

    private final FestivalRepository festivalRepository;
    private final NotificationScheduleRepository scheduleRepository;
    private final NotificationScheduleMapper scheduleMapper;

    @Override
    public NotificationScheduleResponseDTO createSchedule(NotificationScheduleDTO request) {
        Festival festival = festivalRepository.findByFestivalId(request.getFestivalId())
                .orElseThrow(() -> new BusinessException(ErrorCode.FESTIVAL_NOT_FOUND));

        NotificationSchedule schedule = NotificationSchedule.builder()
                .festival(festival)
                .title(request.getTitle())
                .body(request.getBody())
                .sendTime(request.getSendTime())
                .isSent(false)
                .build();

        return scheduleMapper.toDto(scheduleRepository.save(schedule));
    }

    @Override
    public NotificationScheduleResponseDTO updateSchedule(Long id, NotificationScheduleDTO request) {
        NotificationSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        schedule.setTitle(request.getTitle());
        schedule.setBody(request.getBody());
        schedule.setSendTime(request.getSendTime());

        return scheduleMapper.toDto(scheduleRepository.save(schedule));
    }

    @Override
    public void deleteSchedule(Long id) {
        NotificationSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));
        scheduleRepository.delete(schedule);
    }

    @Override
    public List<NotificationScheduleSummaryDTO> getSchedulesByFestival(Long festivalId) {
        List<NotificationSchedule> list = scheduleRepository.findByFestival_FestivalId(festivalId);
        return scheduleMapper.toSummaryDtoList(list);
    }

    @Override
    public List<NotificationScheduleSummaryDTO> getAllSchedules() {
        return scheduleMapper.toSummaryDtoList(scheduleRepository.findAll());
    }
}