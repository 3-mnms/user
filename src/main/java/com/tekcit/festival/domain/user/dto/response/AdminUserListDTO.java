package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.enums.UserGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Schema(description = "운영관리자 일반 사용자 전체 조회 DTO", name = "AdminUserListDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserListDTO {

    @Schema(description = "회원 이름")
    private String name;

    @Schema(description = "로그인 아이디")
    private String loginId;

    @Schema(description = "회원 전화번호")
    private String phone;

    @Schema(description = "회원 이메일")
    private String email;

    @Schema(description = "회원 주민번호 앞자리 + 뒷자리 첫자리")
    private String residentNum;

    @Schema(description = "회원 생년월일")
    private String birth;

    @Schema(description = "회원 성별")
    private UserGender gender;

    @Schema(description = "회원 주소")
    private List<AddressDTO> addresses;

    @Schema(description = "회원 계정 상태(true면 활성화)")
    private boolean isActive;

    @Schema(hidden = true)
    public static AdminUserListDTO fromUserEntity(User user) {
        UserProfile userProfile = user.getUserProfile();
        List<AddressDTO> addresses = userProfile.getAddresses().stream()
                .map(AddressDTO::fromEntity)
                .toList();

        return AdminUserListDTO.builder()
                .name(user.getName())
                .loginId(user.getLoginId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .residentNum(userProfile.getResidentNum())
                .birth(userProfile.getBirth())
                .gender(userProfile.getGender())
                .addresses(addresses)
                .isActive(userProfile.isActive())
                .build();
    }
}
