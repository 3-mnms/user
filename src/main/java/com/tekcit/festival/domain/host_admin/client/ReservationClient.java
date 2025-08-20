package com.tekcit.festival.domain.host_admin.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationClient {

    private final WebClient reservationWebClient; // ** Bean 구성 시 baseUrl 설정

    public List<Long> getAttendeeUserIds(String fid, LocalDateTime startAt) { // **
        // 예시: GET /api/reservations/attendees?fid=PF000123&startAt=2025-08-07T18:00:00
        return reservationWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/reservations/attendees")
                        .queryParam("fid", fid)
                        .queryParam("startAt", startAt) // ISO-8601로 직렬화
                        .build())
                .retrieve()
                .bodyToMono(AttendeesResponse.class)
                .map(AttendeesResponse::userIds)
                .defaultIfEmpty(List.of())
                .block();
    }

    public record AttendeesResponse(String fid, String startsAt, List<Long> userIds) {}
}
