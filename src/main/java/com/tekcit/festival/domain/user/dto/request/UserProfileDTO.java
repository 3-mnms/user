package com.tekcit.festival.domain.user.dto.request;

import com.tekcit.festival.domain.user.entity.Address;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.enums.UserGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "일반 유저 정보 DTO", name = "UserProfileDTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {

    @Schema(description = "회원 주민번호 앞자리 + 뒷자리 첫자리")
    @NotBlank(message = "주민번호는 필수 입력사항 입니다.")
    @Pattern(regexp = "^\\d{6}-[1-4]$",
            message = "주민번호 형식은 6자리-성별코드(1~4)여야 합니다. 예: 990101-1")
    private String residentNum;

    @Schema(description = "회원 주소")
    @NotBlank(message = "주소는 필수 입력사항 입니다.")
    private String address;

    @Schema(description = "회원 주소(우편 번호)")
    @NotBlank(message = "우편 번호는 필수입니다.")
    private String zipCode;

    public UserProfile toEntity(int age, UserGender gender, String birth){
        return UserProfile.builder()
                .residentNum(residentNum)
                .age(age)
                .gender(gender)
                .birth(birth)
                .build();
    }

    public Address toAddressEntity(User user, UserProfile userProfile){
        return Address.builder()
                .address(address)
                .zipCode(zipCode)
                .name(user.getName())
                .phone(user.getPhone())
                .isDefault(true)
                .userProfile(userProfile)
                .build();
    }

}
