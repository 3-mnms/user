package com.tekcit.festival.domain.host_admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    private String festivalId;
    private LocalDateTime performanceDate;
}