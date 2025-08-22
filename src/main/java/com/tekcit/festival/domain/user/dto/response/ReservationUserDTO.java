package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "예매자 정보 DTO", name = "ReservationUserDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationUserDTO {
    @Schema(description = "예매자 userId")
    private Long userId;

    @Schema(description = "예매자 이름")
    private String name;

    @Schema(description = "예매자 전화번호")
    private String phone;

    @Schema(hidden = true)
    public static ReservationUserDTO fromUserEntity(User user) {
        return ReservationUserDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .phone(user.getPhone())
                .build();
    }
}