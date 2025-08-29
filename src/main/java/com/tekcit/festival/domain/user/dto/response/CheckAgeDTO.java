package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "나이 확인 응답 DTO", name = "CheckAgeDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckAgeDTO {
    @Schema(description = "사용자 나이")
    private int age;

    @Schema(hidden = true)
    public static CheckAgeDTO fromEntity(UserProfile userProfile) {
        return CheckAgeDTO.builder()
                .age(userProfile.getAge())
                .build();
    }
}
