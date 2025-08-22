package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "예매 시 사용자 정보 응답 DTO", name = "BookingProfileDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingProfileDTO {
    @Schema(description = "예매자 이메일 주소")
    private String email;

    @Schema(hidden = true)
    public static BookingProfileDTO fromEntity(User bookingUser) {
        return BookingProfileDTO.builder()
                .email(bookingUser.getEmail())
                .build();
    }
}