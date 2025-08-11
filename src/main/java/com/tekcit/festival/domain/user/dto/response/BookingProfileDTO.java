package com.tekcit.festival.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "예매 시 사용자 정보 응답 DTO", name = "BookingProfileDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingProfileDTO {
    @Schema(description = "예매자 이메일 주소")
    private String email;

    @Schema(description = "예매자 전화 번호")
    private String phone;

    @Schema(description = "예매자 주소")
    private List<AddressDTO> addresses;

    @Schema(description = "예매자 생년월일")
    private String birth;

}
