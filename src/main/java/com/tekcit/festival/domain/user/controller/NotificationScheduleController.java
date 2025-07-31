package com.tekcit.festival.domain.user.controller;

import com.tekcit.festival.domain.user.dto.request.NotificationScheduleDTO;
import com.tekcit.festival.domain.user.dto.response.NotificationScheduleResponseDTO;
import com.tekcit.festival.domain.user.dto.response.NotificationScheduleSummaryDTO;
import com.tekcit.festival.domain.user.entity.Festival;
import com.tekcit.festival.domain.user.entity.NotificationSchedule;
import com.tekcit.festival.domain.user.mapper.NotificationScheduleMapper;
import com.tekcit.festival.domain.user.repository.FestivalRepository;
import com.tekcit.festival.domain.user.repository.NotificationScheduleRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "공연 알림 예약", description = "공연 알림 예약 CRUD API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class NotificationScheduleController {

    private final NotificationScheduleRepository scheduleRepository;
    private final FestivalRepository festivalRepository;
    private final NotificationScheduleMapper scheduleMapper;

    @Operation(summary = "예약 등록", description = "공연 FCM 예약 알림을 등록합니다.")
    @PostMapping
    public ResponseEntity<NotificationScheduleResponseDTO> createSchedule(@Valid @RequestBody NotificationScheduleDTO request) {
        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 공연입니다."));

        // 하루 50건 제한 확인
        LocalDateTime startOfDay = request.getSendTime().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        long count = scheduleRepository.countByFestivalAndSendTimeBetween(festival, startOfDay, endOfDay);
        if (count >= 50) {
            return ResponseEntity.badRequest().build();
        }

        NotificationSchedule schedule = new NotificationSchedule();
        schedule.setFestival(festival);
        schedule.setTitle(request.getTitle());
        schedule.setBody(request.getBody());
        schedule.setSendTime(request.getSendTime());
        scheduleRepository.save(schedule);

        return ResponseEntity.ok(scheduleMapper.toDto(schedule));
    }

    @Operation(summary = "예약 수정", description = "예약된 알림을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody NotificationScheduleDTO request) {
        NotificationSchedule schedule = scheduleRepository.findById(id).orElse(null);
        if (schedule == null) {
            return ResponseEntity.status(404).body("❌ 해당 스케줄 없음");
        }

        Festival festival = festivalRepository.findById(request.getFestivalId())
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 공연입니다."));
        schedule.setFestival(festival);
        schedule.setTitle(request.getTitle());
        schedule.setBody(request.getBody());
        schedule.setSendTime(request.getSendTime());
        scheduleRepository.save(schedule);

        return ResponseEntity.ok(scheduleMapper.toDto(schedule));
    }

    @Operation(summary = "예약 삭제", description = "예약 알림을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        return scheduleRepository.findById(id)
                .map(schedule -> {
                    scheduleRepository.delete(schedule);
                    return ResponseEntity.ok("🗑️ 예약 삭제 완료");
                })
                .orElse(ResponseEntity.status(404).body("❌ 해당 스케줄 없음"));
    }

    @Operation(summary = "전체 예약 조회", description = "모든 예약 알림을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<NotificationScheduleSummaryDTO>> getAllSchedules() {
        List<NotificationSchedule> schedules = scheduleRepository.findAll();
        return ResponseEntity.ok(scheduleMapper.toSummaryDtoList(schedules));
    }

    @Operation(summary = "예약 단건 조회", description = "ID로 예약 알림을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable Long id) {
        NotificationSchedule schedule = scheduleRepository.findById(id).orElse(null);
        if (schedule == null) {
            return ResponseEntity.status(404).body("❌ 해당 스케줄 없음");
        }
        return ResponseEntity.ok(scheduleMapper.toDto(schedule));
    }

    @Operation(summary = "공연별 예약 조회", description = "공연 ID로 예약 알림을 조회합니다.")
    @GetMapping("/festival/{festivalId}")
    public ResponseEntity<List<NotificationScheduleSummaryDTO>> getSchedulesByFestivalId(@PathVariable Long festivalId) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 공연입니다."));
        return ResponseEntity.ok(scheduleMapper.toSummaryDtoList(scheduleRepository.findByFestival(festival)));
    }
}
