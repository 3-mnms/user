package com.tekcit.festival.domain.host_admin.service;

import com.tekcit.festival.domain.host_admin.dto.request.BookingRequestDTO;
import com.tekcit.festival.domain.host_admin.dto.response.BookingInfoDTO;
import com.tekcit.festival.domain.host_admin.entity.NotificationSchedule;
import com.tekcit.festival.domain.host_admin.repository.NotificationScheduleRepository;
import com.tekcit.festival.exception.global.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // REMOVED FOR TRANSACTIONAL ON THIS CLASS
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

    @Scheduled(cron = "0 * * * * *")
    public void sendScheduledNotifications() {
        log.info("⏰ 예약 알림 스케줄러 실행: {}", LocalDateTime.now());

        // 현재 시간 10분 뒤를 기준으로 스케줄을 조회
        LocalDateTime scheduledTime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).truncatedTo(ChronoUnit.MINUTES).plusMinutes(10);
        List<NotificationSchedule> schedules = scheduleRepository.findBySendTime(scheduledTime);

        if (schedules.isEmpty()) {
            log.info("ℹ️ 해당 시간에 발송될 예약 알림이 없습니다.");
            return;
        }

        schedules.forEach(schedule -> {
            BookingRequestDTO requestDTO = new BookingRequestDTO(
                    schedule.getFid(),
                    schedule.getStartAt()
            );

            // WebClient 호출을 병렬로 처리하여 스케줄러가 블로킹되지 않도록 함
            bookingWebClient.post()
                    .uri("/api/host/list")
                    .body(Mono.just(requestDTO), BookingRequestDTO.class)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<SuccessResponse<List<Long>>>() {})
                    .publishOn(Schedulers.boundedElastic())
                    .doOnSuccess(response -> {
                        if (response.isSuccess()) {
                            log.info("✅ Booking MSA에서 예매자 정보 수신 성공. 사용자 수: {}", response.getData().size());

                            List<BookingInfoDTO> bookingInfos = response.getData().stream()
                                    .map(userId -> new BookingInfoDTO(
                                            userId,
                                            schedule.getFid(),
                                            schedule.getTitle(),
                                            schedule.getBody()
                                    ))
                                    .collect(Collectors.toList());

                            notificationService.sendNotifications(bookingInfos);
                        } else {
                            log.error("❌ Booking MSA에서 예매자 정보 수신 실패: {}", response.getMessage());
                        }
                    })
                    .doOnError(throwable -> log.error("❌ Booking MSA API 호출 중 오류 발생: {}", throwable.getMessage(), throwable))
                    .subscribe();
        });
    }
}