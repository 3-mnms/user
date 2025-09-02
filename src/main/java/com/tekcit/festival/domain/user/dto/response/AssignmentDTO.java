package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "양도 시 사용자 정보 DTO", name = "AssignmentDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDTO {
    @Schema(description = "사용자 id")
    private Long userId;

    @Schema(description = "사용자 이름")
    private String name;

    @Schema(description = "회원 주민번호 앞자리 + 뒷자리 첫자리")
    private String residentNum;

    @Schema(hidden = true)
    public static AssignmentDTO fromUserEntity(User user) {
        UserProfile userProfile = user.getUserProfile();
        return AssignmentDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .residentNum(userProfile.getResidentNum())
                .build();
    }
}
