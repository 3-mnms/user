package com.tekcit.festival.domain.user.dto.response;

import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.enums.UserGender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Schema(description = "마이페이지 일반 사용자 조회 DTO", name = "MyPageUserDTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MyPageUserDTO extends MyPageCommonDTO{
    // 일반 유저 userProfile
    @Schema(description = "회원 주민번호 앞자리 + 뒷자리 첫자리")
    private String residentNum;

    @Schema(description = "회원 생년월일")
    private String birth;

    @Schema(description = "회원 성별")
    private UserGender gender;

    @Schema(description = "회원 나이")
    private int age;

    @Schema(description = "회원 주소")
    private List<AddressDTO> addresses;

    @Schema(hidden = true)
    public static MyPageUserDTO fromUserEntity(User user, UserProfile userProfile, List<AddressDTO> addresses) {
        return MyPageUserDTO.builder()
                .loginId(user.getLoginId())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .oauthProvider(user.getOauthProvider())
                .residentNum(userProfile.getResidentNum())
                .birth(userProfile.getBirth())
                .gender(userProfile.getGender())
                .age(userProfile.getAge())
                .addresses(addresses)
                .build();
    }
}
