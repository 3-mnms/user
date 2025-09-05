package com.tekcit.festival.domain.user.scheduler;

import com.tekcit.festival.domain.user.service.UserGeocodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserGeocodeScheduler {
    private final UserGeocodeService userGeocodeService;

    @Scheduled(
            fixedDelayString = "${user.geocode.fixed-delay-ms}",
            initialDelayString = "${user.geocode.initial-delay-ms}")
    public void runGeocode() {
        userGeocodeService.geocodeBatch(100); // 최대 100건
    }
}
