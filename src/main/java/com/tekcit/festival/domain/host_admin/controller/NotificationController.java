package com.tekcit.festival.domain.host_admin.controller;

import com.tekcit.festival.domain.host_admin.dto.response.NotificationResponseDTO;
import com.tekcit.festival.domain.host_admin.service.NotificationService;
import com.tekcit.festival.exception.global.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "공연 알림 히스토리 조회", description = "사용자별 수신한 알림 히스토리 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/notice")
public class NotificationController {

    private final NotificationService notificationService;

    public static class NotificationListDTO {
        private Long id;
        private LocalDateTime sentAt;

        public NotificationListDTO(Long id, LocalDateTime sentAt) {
            this.id = id;
            this.sentAt = sentAt;
        }
    }

    @Operation(summary = "알림 히스토리 전체 조회", description = "사용자는 자신이 수신한 알림 목록을 보낸 날짜를 기준으로 최신순으로 조회할 수 있습니다.")
    @GetMapping("/history")
    public ResponseEntity<SuccessResponse<List<NotificationListDTO>>> getUserNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        List<NotificationResponseDTO> notifications = notificationService.getUserNotifications(userId);
        List<NotificationListDTO> notificationList = notifications.stream()
                .map(n -> new NotificationListDTO(n.getId(), n.getSentAt()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new SuccessResponse<>(true, notificationList, "알림 목록을 성공적으로 조회했습니다."));
    }

    @Operation(summary = "알림 상세 조회", description = "특정 알림의 모든 상세 정보를 조회합니다. (제목, 내용, 날짜 등)")
    @GetMapping("/{notificationId}")
    public ResponseEntity<SuccessResponse<NotificationResponseDTO>> getNotificationDetail(@PathVariable Long notificationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        NotificationResponseDTO responseDTO = notificationService.getNotificationDetail(notificationId, userId);
        return ResponseEntity.ok(new SuccessResponse<>(true, responseDTO, "알림 상세 정보를 성공적으로 조회했습니다."));
    }
}