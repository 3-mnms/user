package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.domain.user.dto.request.CheckPwDTO;
import com.tekcit.festival.domain.user.dto.request.ResetPwDTO;
import com.tekcit.festival.domain.user.dto.request.UpdateUserRequestDTO;
import com.tekcit.festival.domain.user.dto.response.*;
import com.tekcit.festival.domain.user.entity.Address;
import com.tekcit.festival.domain.user.entity.HostProfile;
import com.tekcit.festival.domain.user.entity.User;
import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.enums.OAuthProvider;
import com.tekcit.festival.domain.user.enums.UserRole;
import com.tekcit.festival.domain.user.repository.AddressRepository;
import com.tekcit.festival.domain.user.repository.HostProfileRepository;
import com.tekcit.festival.domain.user.repository.UserProfileRepository;
import com.tekcit.festival.domain.user.repository.UserRepository;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final HostProfileRepository hostProfileRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    public Object getUserInfo(Long userId){
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(findUser.getRole() == UserRole.USER) {
            UserProfile profile = userProfileRepository.findByUser_UserId(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            List<Address> addresses = addressRepository.findAllByUserProfile(profile);

            List<AddressDTO> addressDTOS = addresses.stream()
                    .map(address->AddressDTO.fromEntity(address))
                    .toList();
            return MyPageUserDTO.fromUserEntity(findUser, profile, addressDTOS);
        }
        else if(findUser.getRole() == UserRole.HOST){
            HostProfile profile = hostProfileRepository.findByUser_UserId(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            return MyPageHostDTO.fromHostEntity(findUser, profile);
        }

        return MyPageCommonDTO.fromAdminEntity(findUser);
    }

    @Transactional
    public UpdateUserResponseDTO updateUser(UpdateUserRequestDTO updateUserRequestDTO, Long userId){
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UserProfile findProfile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        findUser.setName(updateUserRequestDTO.getName());
        findUser.setPhone(updateUserRequestDTO.getPhone());

        String rNum = updateUserRequestDTO.getResidentNum();
        findProfile.updateResidentInfo(rNum);

        userRepository.save(findUser);
        userProfileRepository.save(findProfile);

        return UpdateUserResponseDTO.fromUserEntity(findUser, findProfile);
    }

    public void checkPassword(Long userId, CheckPwDTO checkPwDTO){
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (findUser.getOauthProvider() != OAuthProvider.LOCAL) {
            throw new BusinessException(ErrorCode.AUTH_NOT_ALLOWED, "카카오 계정은 비밀번호 검증이 없습니다.");
        }

        if (!passwordEncoder.matches(checkPwDTO.getLoginPw(), findUser.getLoginPw())) {
            throw new BusinessException(ErrorCode.AUTH_PASSWORD_NOT_EQUAL_ERROR);
        }
    }

    @Transactional
    public void resetPassword(Long userId, ResetPwDTO resetPwDTO){
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (findUser.getOauthProvider() != OAuthProvider.LOCAL) {
            throw new BusinessException(ErrorCode.AUTH_NOT_ALLOWED, "카카오 계정은 비밀번호가 없습니다.");
        }

        findUser.setLoginPw(passwordEncoder.encode(resetPwDTO.getLoginPw()));
        userRepository.save(findUser);
    }
}
