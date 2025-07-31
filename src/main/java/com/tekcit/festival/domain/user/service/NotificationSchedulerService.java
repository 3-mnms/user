package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.domain.user.entity.NotificationSchedule;
import com.tekcit.festival.domain.user.entity.Reservation;
import com.tekcit.festival.domain.user.entity.Festival;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.repository.FcmTokenRepository;
import com.tekcit.festival.domain.user.repository.NotificationScheduleRepository;
import com.tekcit.festival.domain.user.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class NotificationSchedulerService {

    private final NotificationScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;
    private final FcmService fcmService;
    private final FcmTokenRepository fcmTokenRepository;

    @Scheduled(fixedRate = 60000)
    public void checkAndSendScheduledNotices() {
        List<NotificationSchedule> schedules =
                scheduleRepository.findByIsSentFalseAndSendTimeBefore(LocalDateTime.now());

        for (NotificationSchedule schedule : schedules) {
            try {
                Long festivalId = schedule.getFestival().getId();
                String title = schedule.getTitle();
                String body = schedule.getBody();

                // ✅ 최적화된 방식으로 한 번에 토큰 조회 및 발송
                List<String> tokens = fcmTokenRepository.findTokensByFestival(schedule.getFestival());
                for (String token : tokens) {
                    fcmService.sendMessage(token, title, body);
                }

                schedule.setSent(true); // 전송 여부 업데이트
                scheduleRepository.save(schedule);

                log.info("🔔 [{}] 공연({}) 예약 메시지 전송 완료 ({}명 대상)", title, festivalId, tokens.size());

            } catch (Exception e) {
                log.error("❌ 예약 메시지 전송 중 오류 발생: scheduleId={}", schedule.getScheduleId(), e);
            }
        }
    }
}
