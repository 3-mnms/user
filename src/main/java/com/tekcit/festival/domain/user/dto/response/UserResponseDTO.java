package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "회원 가입 후 응답 DTO", name = "UserResponseDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long userId;

    private String name;

    private UserRole role;

    private LocalDateTime createdAt;
    @Schema(hidden = true)
    public static UserResponseDTO fromEntity(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

