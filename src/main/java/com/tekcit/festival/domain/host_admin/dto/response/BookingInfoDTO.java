package com.tekcit.festival.domain.host_admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingInfoDTO {
    private Long userId;
    private String festivalId;
    private String notificationTitle;
    private String notificationBody;
}