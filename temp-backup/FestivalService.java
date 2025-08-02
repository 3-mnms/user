package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.domain.exception.global.BusinessException;
import com.tekcit.festival.domain.exception.global.ErrorCode;
import com.tekcit.festival.domain.user.dto.FestivalDto;
import com.tekcit.festival.domain.user.dto.request.NotificationScheduleDTO;
import com.tekcit.festival.domain.user.dto.response.NotificationScheduleResponseDTO;
import com.tekcit.festival.domain.user.dto.response.NotificationScheduleSummaryDTO;
import com.tekcit.festival.domain.user.entity.Festival;
import com.tekcit.festival.domain.user.entity.NotificationSchedule;
import com.tekcit.festival.domain.user.mapper.FestivalMapper;
import com.tekcit.festival.domain.user.mapper.NotificationScheduleMapper;
import com.tekcit.festival.domain.user.repository.FestivalRepository;
import com.tekcit.festival.domain.user.repository.NotificationScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FestivalService {
    Festival createFestival(Festival festival);
    Festival updateFestival(Long festivalId, Festival request);
    void deleteFestivalByHost(Long festivalId, Long hostId);
    List<Festival> getFestivalsByHost(Long hostId);
    List<Festival> getAllFestivals();
    void adminDeleteFestival(Long festivalId);
    Optional<Festival> getFestivalDetail(Long festivalId);
    List<String> getCategories();
}