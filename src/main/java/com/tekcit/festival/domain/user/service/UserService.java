package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.domain.user.dto.request.SignupUserDTO;
import com.tekcit.festival.domain.user.dto.request.UserProfileDTO;
import com.tekcit.festival.domain.user.dto.response.UserResponseDTO;
import com.tekcit.festival.domain.user.entity.HostProfile;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.enums.UserGender;
import com.tekcit.festival.domain.user.repository.UserRepository;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO signupUser(@Valid SignupUserDTO signupUserDTO){
        validateDuplicate(signupUserDTO);

        User user = signupUserDTO.toUserEntity();
        user.setLoginPw(passwordEncoder.encode(user.getLoginPw()));

        UserProfileDTO userProfileDTO = signupUserDTO.getUserProfile();
        String rNum = userProfileDTO.getResidentNum();
        UserProfile userProfile = userProfileDTO.toEntity(calcAge(rNum), extractGender(rNum));

        userProfile.setUser(user);
        user.setUserProfile(userProfile);

        userRepository.save(user);
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional
    public UserResponseDTO signupHost(SignupUserDTO signupUserDTO){
        validateDuplicate(signupUserDTO);

        User user = signupUserDTO.toHostEntity();
        user.setLoginPw(passwordEncoder.encode(user.getLoginPw()));

        HostProfile hostProfile = signupUserDTO.getHostProfile().toEntity();
        hostProfile.setUser(user);
        user.setHostProfile(hostProfile);

        userRepository.save(user);
        return UserResponseDTO.fromEntity(user);
    }

    public int calcAge(String residentNum){
        if (residentNum == null || !residentNum.contains("-")) {
            throw new IllegalArgumentException("올바르지 않은 주민번호 형식입니다.");
        }

        String[] rArray = residentNum.split("-");
        String birth = rArray[0];
        char gender = rArray[1].charAt(0);

        int year = Integer.parseInt(birth.substring(0, 2));
        int month = Integer.parseInt(birth.substring(2, 4));
        int date = Integer.parseInt(birth.substring(4, 6));

        switch(gender) {
            case '1': case '2': case '5': case '6':
                year += 1900;
                break;
            case '3': case '4': case '7': case '8':
                year += 2000;
                break;
            case '9': case '0':
                year += 1800;
                break;
            default:
                throw new IllegalArgumentException("올바르지 않은 성별 코드입니다.");
        }
        int currentYear = LocalDate.now().getYear();
        return currentYear-year+1;
    }

    public UserGender extractGender(String residentNum){
        char g = residentNum.split("-")[1].charAt(0);
        UserGender gender = UserGender.MALE;

        if(g == 1 || g == 3){
            gender = UserGender.MALE;
        }
        else if(g == 2 || g == 4){
            gender = UserGender.FEMALE;
        }

        return gender;
    }

    public void validateDuplicate(SignupUserDTO signupUserDTO){
        if(userRepository.existsByLoginId(signupUserDTO.getLoginId()))
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID, signupUserDTO.getLoginId());
        if(userRepository.existsByPhone(signupUserDTO.getPhone()))
            throw new BusinessException(ErrorCode.DUPLICATE_PHONE_ID, signupUserDTO.getPhone());
        if(userRepository.existsByEmail(signupUserDTO.getEmail()))
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL_ID, signupUserDTO.getEmail());
    }
}