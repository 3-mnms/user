package com.tekcit.festival.domain.host_admin.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tekcit.festival.domain.host_admin.entity.NotificationSchedule;
import com.tekcit.festival.domain.host_admin.repository.NotificationScheduleRepository;
import com.tekcit.festival.domain.host_admin.service.BookingInfoDTO;
import com.tekcit.festival.domain.host_admin.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final WebClient bookingWebClient;
    private final NotificationService notificationService;
    private final NotificationScheduleRepository scheduleRepository;

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void sendScheduledNotifications() {
        log.info("⏰ 예약 알림 스케줄러 실행: {}", LocalDateTime.now());

        LocalDateTime scheduledTime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).truncatedTo(ChronoUnit.MINUTES).plusMinutes(10);
        List<NotificationSchedule> schedules = scheduleRepository.findBySendTime(scheduledTime);

        if (schedules.isEmpty()) {
            log.info("ℹ️ 해당 시간에 발송될 예약 알림이 없습니다.");
            return;
        }

        schedules.forEach(schedule -> {
            // Booking MSA에 보낼 요청 DTO 생성
            BookingRequestDTO requestDTO = new BookingRequestDTO(
                    schedule.getFid(),
                    schedule.getStartAt()
            );

            // Booking MSA API 호출
            bookingWebClient.post()
                    .uri("/api/host/list")
                    .body(Mono.just(requestDTO), BookingRequestDTO.class)
                    .retrieve()
                    // ▼▼▼ Long 목록을 받도록 명시하는 핵심 코드 ▼▼▼
                    .bodyToMono(new ParameterizedTypeReference<SuccessResponse<List<Long>>>() {})
                    .publishOn(Schedulers.boundedElastic())
                    .doOnSuccess(response -> {
                        if (response.isSuccess()) {
                            log.info("✅ Booking MSA에서 예매자 정보 수신 성공. 사용자 수: {}", response.getData().size());

                            // Long 타입의 userId 리스트를 BookingInfoDTO 리스트로 변환
                            List<BookingInfoDTO> bookingInfos = response.getData().stream()
                                    .map(userId -> new BookingInfoDTO(
                                            userId,
                                            schedule.getFid(),
                                            schedule.getTitle(),
                                            schedule.getBody()
                                    ))
                                    .collect(Collectors.toList());

                            // 변환된 DTO 목록을 NotificationService로 전달
                            notificationService.sendNotifications(bookingInfos);
                        } else {
                            log.error("❌ Booking MSA에서 예매자 정보 수신 실패: {}", response.getMessage());
                        }
                    })
                    .doOnError(throwable -> log.error("❌ Booking MSA API 호출 중 오류 발생: {}", throwable.getMessage(), throwable))
                    .subscribe();
        });
    }

    // Booking MSA에 보내는 요청 DTO (내부 클래스로 유지)
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingRequestDTO {
        private String festivalId;
        private LocalDateTime performanceDate;
    }

    // Booking MSA에서 받는 응답 DTO (내부 클래스로 유지)
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SuccessResponse<T> {
        private boolean success;
        private T data;
        private String message;
    }
}