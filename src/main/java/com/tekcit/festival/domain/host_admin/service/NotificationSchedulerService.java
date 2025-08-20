package com.tekcit.festival.domain.host_admin.service;

import com.tekcit.festival.domain.host_admin.repository.FcmTokenRepository;
import com.tekcit.festival.domain.host_admin.service.FcmService;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final FcmService fcmService;
    private final UserRepository userRepository; // Optional, for user lookup if needed

    // reservation MSA의 API 호출 관련 필드 추후 완성되면 추가

    /**
     * ✅ cron 표현식을 사용하여 10분마다 실행
     * 예약된 시간 10분 전에 알림을 보내는 스케줄러.
     * Reservation MSA의 API를 호출하여 해당 시간의 예약자 리스트를 가져옵니다.
     */
    @Scheduled(cron = "0 */10 * * * *") // 매 10분마다 실행
    public void sendReservationNotifications() {
        log.info("⏰ 예약 알림 스케줄러 실행: {}", LocalDateTime.now());

        // Reservation MSA의 API가 아직 완성되지 않았으므로 임시로 Mock 데이터를 사용
        List<Long> mockUserIds = Arrays.asList(1L, 2L, 3L, 4L, 5L); // userId 하드 코딩

        // FCM 서비스로 userId 리스트를 전달하여 알림 전송
        String title = "🥳 페스티벌 시작 10분 전!";
        String body = "곧 페스티벌이 시작됩니다. 준비하세요!";
        fcmService.sendMessageToUsers(mockUserIds, title, body);

        // TODO: Reservation API 완성 시, WebClient 주입 및 API 호출 로직을 추가할 것
        /*
        // 이 부분은 Reservation API 완성 후 다시 추가
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reservationTime = now.plusMinutes(10);
        Long festivalId = 1L;

        webClient.get()
                .uri(reservationServiceUrl + "/api/reservations/festival/{festivalId}/users?startAt={startAt}", festivalId, reservationTime)
                .retrieve()
                .bodyToFlux(Long.class)
                .collectList()
                .doOnSuccess(userIds -> {
                    if (userIds.isEmpty()) {
                        log.info("ℹ️ 해당 시간에 예약된 사용자가 없습니다.");
                        return;
                    }
                    log.info("✅ Reservation MSA로부터 받은 userId 리스트: {}", userIds);

                    fcmService.sendMessageToUsers(userIds, title, body);
                })
                .doOnError(error -> log.error("❌ Reservation MSA API 호출 중 오류 발생: {}", error.getMessage()))
                .subscribe();
        */
    }
}
