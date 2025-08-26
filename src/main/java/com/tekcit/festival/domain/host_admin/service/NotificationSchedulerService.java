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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final WebClient bookingWebClient;
    private final NotificationService notificationService;
    private final NotificationScheduleRepository scheduleRepository;

    // 분산 환경에서 스케줄러 중복 실행을 막기 위한 로컬 락
    private static final Lock schedulerLock = new ReentrantLock();

    //TEST용
    //@Scheduled(cron = "0 * * * * *")
    //10분 마다 스케줄러 실행
    @Scheduled(cron = "0 */10 * * * *")
    @Transactional(readOnly = true)
    public void scheduleNotifications() {
        // 락 획득 시도 (30초 대기)
        try {
            if (!schedulerLock.tryLock(30, TimeUnit.SECONDS)) {
                log.info("다른 인스턴스에서 스케줄러가 이미 실행 중입니다. 스킵합니다.");
                return;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("스케줄러 락 획득 중단", e);
            return;
        }

        try {
            log.info("예약 알림 스케줄러 실행: {}", LocalDateTime.now());

            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul")).truncatedTo(ChronoUnit.MINUTES);
            //TEST용
            //LocalDateTime startOfWindow = now.plusMinutes(1);
            //LocalDateTime endOfWindow = now.plusMinutes(5);

            //13:00에 시작하면 13:01~13:10사이의 스케줄을 확인함
            LocalDateTime startOfWindow = now.plusMinutes(1);
            LocalDateTime endOfWindow = now.plusMinutes(10);

            List<NotificationSchedule> schedules = scheduleRepository.findBySendTimeBetween(startOfWindow, endOfWindow);

            if (schedules.isEmpty()) {
                log.info("{} ~ {} 사이 발송될 예약 알림이 없습니다.", startOfWindow, endOfWindow);
                return;
            }

            schedules.forEach(schedule -> {
                BookingRequestDTO requestDTO = new BookingRequestDTO(
                        schedule.getFid(),
                        schedule.getStartAt()
                );

                long delayInSeconds = Duration.between(now, schedule.getSendTime()).toMillis() / 1000;

                bookingWebClient.post()
                        .uri("/api/host/list")
                        .body(Mono.just(requestDTO), BookingRequestDTO.class)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<SuccessResponse<List<Long>>>() {})
                        .publishOn(Schedulers.boundedElastic())
                        .delaySubscription(Duration.ofSeconds(delayInSeconds))
                        // API 호출 직전에 파라미터 로깅 추가
                        .doOnSubscribe(subscription -> log.info("[API 호출] bookingWebClient 시작. 파라미터: {}", requestDTO))
                        .doOnSuccess(response -> {
                            if (response.isSuccess()) {
                                // API 성공 응답 데이터 직접 로깅 추가
                                log.info("[API 응답] bookingWebClient 성공. 받은 데이터: {} (총 {}명)",
                                        response.getData(), response.getData().size());

                                log.info("{} 알림 발송 시작. 사용자 수: {}", schedule.getSendTime(), response.getData().size(), LocalDateTime.now());

                                List<BookingInfoDTO> bookingInfos = response.getData().stream()
                                        .map(userId -> new BookingInfoDTO(
                                                userId,
                                                schedule.getFid(),
                                                schedule.getTitle(),
                                                schedule.getBody(),
                                                schedule.getFname()
                                        ))
                                        .collect(Collectors.toList());

                                notificationService.sendNotifications(bookingInfos);

                                schedule.setSent(true);
                                scheduleRepository.save(schedule);
                                log.info("알림 발송 확정 및 DB 저장이 완료되었습니다. {}건.", bookingInfos.size());
                            } else {
                                log.error("[API 응답] {} 알림 발송 실패: Booking MSA에서 예매자 정보 수신 실패. 원인: {}", schedule.getSendTime(), response.getMessage());
                            }
                        })
                        .doOnError(throwable -> log.error("[API 오류] {} 알림 발송 실패: Booking MSA API 호출 중 오류 발생. 원인: {}", schedule.getSendTime(), throwable.getMessage(), throwable))
                        .subscribe();
            });

        } finally {
            // 락 해제
            schedulerLock.unlock();
            log.info("스케줄러 락이 해제되었습니다.");
        }
    }
}