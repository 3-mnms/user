package com.tekcit.festival.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "동시 로그인 응답 DTO", name = "LoginConflictDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginConflictDTO {
    @Schema(description = "로그인 confirm ticket")
    private String loginTicket;

    public static LoginConflictDTO fromTicket(String loginTicket) {
        return LoginConflictDTO.builder()
                .loginTicket(loginTicket)
                .build();
    }
}
