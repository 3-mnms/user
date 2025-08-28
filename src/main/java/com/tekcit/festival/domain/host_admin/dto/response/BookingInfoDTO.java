package com.tekcit.festival.domain.host_admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "예매자 정보 응답 DTO")
public class BookingInfoDTO {
    @Schema(description = "유저 ID", example = "1")
    private Long userId;

    @Schema(description = "페스티벌 고유 ID", example = "FB123456")
    private String festivalId;

    @Schema(description = "알림 제목", example = "공연 시작 30분 전!")
    private String notificationTitle;

    @Schema(description = "알림 내용", example = "잠시 후 공연이 시작됩니다. 준비해 주세요.")
    private String notificationBody;

    @Schema(description = "공연명(스냅샷)", example = "뮤지컬 캣츠")
    private String fname;
}