package com.tekcit.festival.domain.host_admin.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingRequestDTO {
    private String festivalId;
    private LocalDateTime performanceDate;
}