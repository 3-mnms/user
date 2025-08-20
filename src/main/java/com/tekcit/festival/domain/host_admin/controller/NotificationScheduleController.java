package com.tekcit.festival.domain.host_admin.controller;

import com.tekcit.festival.domain.host_admin.dto.request.NotificationScheduleDTO;
import com.tekcit.festival.domain.host_admin.dto.response.NotificationScheduleResponseDTO;

import com.tekcit.festival.domain.host_admin.service.NotificationScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "공연 알림 예약", description = "공연 알림 예약 CRUD API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/notice")
public class NotificationScheduleController {

    private final NotificationScheduleService scheduleService;

    @Operation(summary = "예약 등록", description = "공연 FCM 예약 알림을 등록합니다. (HOST만 가능)")
    @PostMapping
    public ResponseEntity<NotificationScheduleResponseDTO> create(
            @Valid @RequestBody NotificationScheduleDTO request,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Id") String userId) {
        if (!"HOST".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long parsedUserId = Long.valueOf(userId);
        return ResponseEntity.ok(scheduleService.create(request, parsedUserId));
    }

    @Operation(summary = "예약 수정", description = "예약된 알림을 수정합니다. (HOST만 가능, 본인 소유만)")
    @PutMapping("/{id}")
    public ResponseEntity<NotificationScheduleResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody NotificationScheduleDTO request,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Id") String userId) {
        if (!"HOST".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long parsedUserId = Long.valueOf(userId);
        return ResponseEntity.ok(scheduleService.update(id, request, parsedUserId));
    }

    @Operation(summary = "예약 삭제", description = "예약 알림을 삭제합니다. (HOST만 가능, 본인 소유만)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Id") String userId) {
        if (!"HOST".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long parsedUserId = Long.valueOf(userId);
        scheduleService.delete(id, parsedUserId);
        return ResponseEntity.ok("🗑️ 예약 삭제 완료");
    }

    @Operation(summary = "전체 예약 조회", description = "모든 예약 알림을 조회합니다. (HOST는 본인 소유만, ADMIN은 전체 )")
    @GetMapping
    public ResponseEntity<List<NotificationScheduleResponseDTO>> getAll(
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Id") String userId) {
        if ("HOST".equals(userRole)) {
            Long parsedUserId = Long.valueOf(userId);
            return ResponseEntity.ok(scheduleService.getByUserId(parsedUserId));
        }
        return ResponseEntity.ok(scheduleService.getAll());
    }

    @Operation(summary = "예약 단건 조회", description = "ID로 예약 알림을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationScheduleResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getById(id));
    }
}