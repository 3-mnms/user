package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "가예매자 DTO", name = "ReservationUserDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreReservationDTO {
    @Schema(description = "가예매자 이름")
    private String name;

    @Schema(description = "가예매자 전화번호")
    private String phone;

    @Schema(description = "가예매자 이메일")
    private String email;

    @Schema(hidden = true)
    public static PreReservationDTO fromUserEntity(User user) {
        return PreReservationDTO.builder()
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }
}
