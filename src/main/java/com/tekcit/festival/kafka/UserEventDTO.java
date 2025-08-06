package com.tekcit.festival.kafka;

import com.tekcit.festival.domain.user.enums.UserEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEventDTO {
    private Long userId;
    private String loginId;
    private String name;
    private String email;
    private UserEventType eventType; // 예: REGISTERED, DELETED 등
    private String accessToken;
}
