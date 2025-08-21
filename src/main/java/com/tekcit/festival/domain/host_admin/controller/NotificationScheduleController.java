package com.tekcit.festival.domain.host_admin.controller;

import com.tekcit.festival.domain.host_admin.dto.request.NotificationScheduleDTO;
import com.tekcit.festival.domain.host_admin.dto.response.NotificationScheduleResponseDTO;
import com.tekcit.festival.domain.host_admin.service.NotificationScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "공연 알림 예약", description = "공연 알림 예약 CRUD API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/notice")
public class NotificationScheduleController {

    private final NotificationScheduleService scheduleService;

    // 공통 로직을 별도의 메서드로 분리
    private Long getUserIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }

    @Operation(summary = "예약 등록", description = "공연 FCM 예약 알림을 등록합니다. (HOST만 가능)")
    @PostMapping
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<NotificationScheduleResponseDTO> create(@Valid @RequestBody NotificationScheduleDTO request) {
        Long userId = getUserIdFromSecurityContext();
        return ResponseEntity.ok(scheduleService.create(request, userId));
    }

    @Operation(summary = "예약 수정", description = "예약된 알림을 수정합니다. (HOST만 가능, 본인 소유만)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<NotificationScheduleResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody NotificationScheduleDTO request) {
        Long userId = getUserIdFromSecurityContext();
        return ResponseEntity.ok(scheduleService.update(id, request, userId));
    }

    @Operation(summary = "예약 삭제", description = "예약 알림을 삭제합니다. (HOST만 가능, 본인 소유만)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        Long userId = getUserIdFromSecurityContext();
        scheduleService.delete(id, userId);
        return ResponseEntity.ok("🗑️ 예약 삭제 완료");
    }

    @Operation(summary = "전체 예약 조회", description = "모든 예약 알림을 조회합니다. (HOST는 본인 소유만, ADMIN은 전체 )")
    @GetMapping
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    public ResponseEntity<List<NotificationScheduleResponseDTO>> getAll() {
        Long userId = getUserIdFromSecurityContext();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(scheduleService.getAll());
        }
        return ResponseEntity.ok(scheduleService.getByUserId(userId));
    }

    @Operation(summary = "예약 단건 조회", description = "ID로 예약 알림을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationScheduleResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getById(id));
    }
}