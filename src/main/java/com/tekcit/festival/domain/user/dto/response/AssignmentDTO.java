package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "양도 시 사용자 이름")
    private String name;

    @Schema(description = "양도 시 사용자 전화번호")
    private String phone;

    @Schema(hidden = true)
    public static AssignmentDTO fromUserEntity(User user) {
        return AssignmentDTO.builder()
                .name(user.getName())
                .phone(user.getPhone())
                .build();
    }
}
