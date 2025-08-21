package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.enums.UserGender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "통계 정보 DTO", name = "StatisticsDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDTO {
    @Schema(description = "회원 성별")
    private UserGender gender;

    @Schema(description = "회원 나이")
    private int age;

    @Schema(hidden = true)
    public static StatisticsDTO fromUserProfileEntity(UserProfile userProfile) {
        return StatisticsDTO.builder()
                .gender(userProfile.getGender())
                .age(userProfile.getAge())
                .build();
    }
}
