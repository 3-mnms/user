package com.tekcit.festival.domain.host_admin.service;

import com.tekcit.festival.domain.host_admin.dto.request.BookingRequestDTO;
import com.tekcit.festival.domain.host_admin.entity.Notification;
import com.tekcit.festival.domain.host_admin.entity.NotificationSchedule;
import com.tekcit.festival.domain.host_admin.repository.NotificationRepository;
import com.tekcit.festival.domain.host_admin.repository.NotificationScheduleRepository;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
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
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final WebClient bookingWebClient;
    private final NotificationRepository notificationRepository;
    private final NotificationScheduleRepository scheduleRepository;
    private final FcmService fcmService;

    // 단일 프로세스 내에서 스케줄러 중복 실행을 막기 위한 로컬 락
    private static final Lock schedulerLock = new ReentrantLock();

    // 프로세스 내에서 동일한 작업이 중복 예약되는 것을 막기 위한 Set
    private static final Set<Long> inFlight = ConcurrentHashMap.newKeySet();

    // 매분 실행되는 스케줄러
    @Scheduled(cron = "0 * * * * *")
    public void scheduleNotifications() {
        // 락 획득 시도 (30초 대기)
        try {
            if (!schedulerLock.tryLock(30, TimeUnit.SECONDS)) {
                log.info("다른 스케줄러 인스턴스가 이미 실행 중입니다. 작업을 건너뜁니다.");
                return;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("스케줄러 락 획득 중단", e);
            return;
        }

        try {
            final ZoneId KST = ZoneId.of("Asia/Seoul");
            log.info("스케줄러 실행 시각: {}", LocalDateTime.now(KST));

            // 현재 시각으로부터 1분 이내의 발송 예정 스케줄을 조회
            LocalDateTime now = LocalDateTime.now(KST).truncatedTo(ChronoUnit.MINUTES);
            LocalDateTime endOfWindow = now.plusMinutes(1);

            // 아직 발송되지 않은 알림 스케줄만 DB에서 가져옴
            List<NotificationSchedule> schedules = scheduleRepository.findBySendTimeBetweenAndIsSentFalse(now, endOfWindow);
            if (schedules.isEmpty()) {
                log.info("이번 분에 발송될 알림이 없습니다.");
                return;
            }

            schedules.forEach(schedule -> {
                Long scheduleId = schedule.getScheduleId();
                // 해당 작업이 이미 예약되었는지 확인하여 중복 방지
                if (!inFlight.add(scheduleId)) {
                    log.info("스케줄 ID {}는 이미 처리 중입니다. 건너뜁니다.", scheduleId);
                    return;
                }

                // 발송 시각이 지났을 경우 지연 시간을 0으로 설정
                long delayInSeconds = Math.max(0, Duration.between(LocalDateTime.now(KST), schedule.getSendTime()).getSeconds());

                // WebClient를 이용한 비동기 작업 시작
                bookingWebClient.post()
                        .uri("/api/host/list")
                        .bodyValue(new BookingRequestDTO(schedule.getFid(), schedule.getStartAt()))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<SuccessResponse<List<Long>>>() {})
                        .timeout(Duration.ofSeconds(5)) // 5초 타임아웃
                        .retryWhen(Retry.backoff(3, Duration.ofMillis(500))) // 최대 3회 재시도 (500ms 간격으로 지수 백오프)
                        .publishOn(Schedulers.boundedElastic()) // JPA와 같은 블로킹 작업 보호
                        .delaySubscription(Duration.ofSeconds(delayInSeconds)) // 발송 시각까지 대기
                        .doOnSuccess(response -> {
                            // API 호출 성공 시 처리 로직
                            // API 호출 성공 시 처리 로직
                            if (response.isSuccess() && response.getData() != null && !response.getData().isEmpty()) {
                                log.info("스케줄 ID {}에 대한 API 호출 성공. {}명의 사용자를 찾았습니다.", scheduleId, response.getData().size());

                                // Add this line to log the list of user IDs.
                                log.info("스케줄 ID {}에 대한 예매자 리스트: {}", scheduleId, response.getData());

                                // 알림 발송 및 DB 저장 (트랜잭션으로 처리)
                                sendAndSaveNotifications(schedule, response.getData());
                            } else {
                                log.warn("스케줄 ID {}에 대한 API 응답 실패 또는 사용자 없음. 메시지: {}", scheduleId, response.getMessage());
                                // 사용자가 0명이라도 스케줄 상태를 '발송 완료'로 업데이트하여 재처리 방지
                                updateScheduleStatus(schedule, true);;
                            }
                        })
                        .doOnError(error -> {
                            // API 호출 실패 시 처리 로직
                            log.error("스케줄 ID {} 알림 처리 중 API 호출 실패. 원인: {}", scheduleId, error.getMessage(), error);
                            // WebClient 통신 실패 시에도 알림 스케줄의 상태를 '실패'로 업데이트하는 로직 추가 가능
                            // 현재는 doFinally에서 inFlight 제거만 하고, 스케줄 상태는 변경하지 않음
                            throw new BusinessException(ErrorCode.API_CALL_FAILED);
                        })
                        .doFinally(signal -> inFlight.remove(scheduleId)) // 작업 완료 후 Set에서 ID 제거
                        .subscribe(
                                result -> log.info("스케줄 ID {} 알림 처리가 완료되었습니다.", scheduleId),
                                error -> log.error("스케줄 ID {} 알림 처리 중 오류 발생. 원인: {}", scheduleId, error.getMessage(), error)
                        );
            });
        } finally {
            // 스케줄러 락 해제
            schedulerLock.unlock();
            log.info("스케줄러 락이 해제되었습니다.");
        }
    }

    // 알림 발송 및 DB 저장을 담당하는 트랜잭션 메서드
    @Transactional
    public void sendAndSaveNotifications(NotificationSchedule schedule, List<Long> userIds) {
        // FCM을 통해 알림 발송
        String finalTitle = String.format("[%s] %s", schedule.getFname(), schedule.getTitle());
        log.info("FCM 발송 직전 제목: '{}', 내용: '{}'", finalTitle, schedule.getBody());

        fcmService.sendMessageToUsers(userIds, finalTitle, schedule.getBody());

        // 알림 내역을 Notification 테이블에 저장
        List<Notification> notificationsToSave = userIds.stream()
                .map(userId -> Notification.builder()
                        .userId(userId)
                        .title(schedule.getTitle())
                        .body(schedule.getBody())
                        .fname(schedule.getFname())
                        .build())
                .collect(Collectors.toList());
        notificationRepository.saveAll(notificationsToSave);

        // 스케줄 상태를 '발송 완료'로 업데이트
        schedule.setSent(true);
        scheduleRepository.save(schedule);

        log.info("스케줄 ID {}에 대한 알림 내역 {}건이 DB에 저장되었습니다.", schedule.getScheduleId(), notificationsToSave.size());
    }

    // 스케줄 상태만 업데이트하는 트랜잭션 메서드 (알림 발송 대상이 없을 때 사용)
    @Transactional
    public void updateScheduleStatus(NotificationSchedule schedule, boolean status) {
        schedule.setSent(status);
        scheduleRepository.save(schedule);
    }
}