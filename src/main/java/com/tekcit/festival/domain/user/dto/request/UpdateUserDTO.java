package com.tekcit.festival.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "회원 정보 수정 요청 DTO", name = "UpdateUserDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserDTO {
    @Schema(description = "회원 이름")
    @NotBlank(message = "이름은 필수 입력사항 입니다.")
    private String name;

    @Schema(description = "회원 전화번호")
    @NotBlank(message = "전화번호는 필수 입력사항 입니다.")
    @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$",
            message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678")
    private String phone;

    @Schema(description = "회원 주민번호 앞자리 + 뒷자리 첫자리")
    @NotBlank(message = "주민번호는 필수 입력사항 입니다.")
    @Pattern(regexp = "^\\d{6}-[1-4]$",
            message = "주민번호 형식은 6자리-성별코드(1~4)여야 합니다. 예: 990101-1")
    private String residentNum;


}
