package com.tekcit.festival.domain.host_admin.controller;

import com.tekcit.festival.domain.host_admin.dto.request.NotificationScheduleDTO;
import com.tekcit.festival.domain.host_admin.dto.request.NotificationUpdateScheduleDTO;
import com.tekcit.festival.domain.host_admin.dto.response.NotificationScheduleResponseDTO;
import com.tekcit.festival.domain.host_admin.service.NotificationScheduleService;
import com.tekcit.festival.exception.global.SuccessResponse;
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

@Tag(name = "공연 공지 알림 등록", description = "공연 공지 알림 등록 CRUD API")
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

    @Operation(summary = "공지 알림 등록", description = "공연 FCM 공지 알림을 등록합니다. (HOST만 가능)")
    @PostMapping
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<SuccessResponse<NotificationScheduleResponseDTO>> create(@Valid @RequestBody NotificationScheduleDTO request) {
        Long userId = getUserIdFromSecurityContext();
        NotificationScheduleResponseDTO data = scheduleService.create(request, userId);
        return ResponseEntity.ok(new SuccessResponse<>(true, data, "🎉 공지 알림 등록 완료"));
    }

    @Operation( summary = "공지 알림 수정", description = "실행되지 않는 등록된 알림에 한해 제목/내용/발송시각만 부분 수정합니다. (HOST만 가능, 본인 소유만)")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<SuccessResponse<NotificationScheduleResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody NotificationUpdateScheduleDTO request
    ) {
        Long userId = getUserIdFromSecurityContext();
        NotificationScheduleResponseDTO data = scheduleService.update(id, request, userId);
        return ResponseEntity.ok(new SuccessResponse<>(true, data, "✏️ 공지 알림 수정 완료"));
    }

    @Operation(summary = "공지 알림 삭제", description = "공지 스케줄은 삭제 하지만, 유저에서 발송된 히스토리는 삭제 X. (HOST는 본인 소유만, ADMIN은 전체 )")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    public ResponseEntity<SuccessResponse<Void>> delete(@PathVariable Long id) {
        Long userId = getUserIdFromSecurityContext();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        scheduleService.delete(id, userId, isAdmin);
        return ResponseEntity.ok(new SuccessResponse<>(true, null, "🗑️ 공지 알림 삭제 완료"));
    }

    @Operation(summary = "전체 공지 알림 조회", description = "모든 공지 알림을 조회합니다. (HOST는 본인 소유만, ADMIN은 전체 )")
    @GetMapping
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    public ResponseEntity<SuccessResponse<List<NotificationScheduleResponseDTO>>> getAll() {
        Long userId = getUserIdFromSecurityContext();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<NotificationScheduleResponseDTO> data;
        if (isAdmin) {
            data = scheduleService.getAll();
        } else {
            data = scheduleService.getByUserId(userId);
        }
        return ResponseEntity.ok(new SuccessResponse<>(true, data, "✅ 공지 알림 조회 완료"));
    }

    @Operation(summary = "전체 공지 단건 조회", description = "ID로 전체 공지 알림을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<NotificationScheduleResponseDTO>> getById(@PathVariable Long id) {
        NotificationScheduleResponseDTO data = scheduleService.getById(id);
        return ResponseEntity.ok(new SuccessResponse<>(true, data, "✅ 공지 알림 조회 완료"));
    }
}